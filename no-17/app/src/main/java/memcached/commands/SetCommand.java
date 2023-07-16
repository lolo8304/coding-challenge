package memcached.commands;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.logging.Logger;

import memcached.server.cache.ExpireIn;
import memcached.server.cache.MemCache;

public class SetCommand extends DataCommand {
    private static final Logger _logger = Logger.getLogger(SetCommand.class.getName());
    private Optional<Duration> ttl;
    private Optional<Instant> expirationTime;

    private static CommandLine getCommandLine(String key, String value) {
        return getCommandLine(key, value, 0, 0, false);
    }

    private static CommandLine getCommandLine(String key, String value, int flags, int exptime, boolean noreply) {
        return new CommandLine(
                String.format("set %s %d %d %d %s", key, flags, exptime, value.length(), noreply ? "noreply" : ""));
    }

    public SetCommand(CommandLine commandLine, Data data) {
        super(commandLine, data);
        initTtl();
    }

    public SetCommand(String... tokens) {
        super(tokens);
        initTtl();
    }

    public SetCommand(String key, String value) {
        super(getCommandLine(key, value), new Data(value));
        initTtl();
    }

    public SetCommand(String key, String value, int flags, int exptime, boolean noreply) {
        super(getCommandLine(key, value, flags, exptime, noreply), new Data(value));
        initTtl();
    }

    private void initTtl() {
        var expInS = this.exptime();
        if (expInS != 0) {
            this.ttl = Optional.of(ExpireIn.seconds(expInS).duration);
            this.expirationTime = Optional.of(Instant.now().plus(this.ttl.get()));
        } else {
            this.ttl = Optional.empty();
            this.expirationTime = Optional.empty();
        }
    }

    @Override
    public void validate() throws ValidationException {
        if (this.key == null || this.key.isBlank()) {
            throw new ValidationException("Key is mandatory, but null or empty");
        }
        if (this.commandLine == null) {
            throw new ValidationException("Commandline is empty");
        }
        if (this.type == null || this.type.isBlank()) {
            throw new ValidationException("Type is mandatory, but null or empty");
        }
        if (this.flags() < 0) {
            throw new ValidationException("Flags must be >= 0");
        }
        if (this.exptime() < 0) {
            throw new ValidationException("exptime must be >= 0");
        }
        var b = this.bytes();
        if (b < 0) {
            throw new ValidationException("bytes must be >= 0");
        }
        if (b != this.data.data.length()) {
            throw new ValidationException(String.format("bytes must be length of data (expected '%d', received '%d')",
                    this.data.data.length(), b));
        }
    }

    public boolean isAlive() {
        return this.ttl.isEmpty() || this.expirationTime.get().isAfter(Instant.now());
    }

    public boolean isExpired() {
        return !isAlive();
    }

    public boolean willExpireIn(Duration duration) {
        return this.ttl.isPresent() && this.expirationTime.get().isBefore(Instant.now().plus(duration));
    }

    public boolean willExpireAt(Instant expireAt) {
        return this.ttl.isPresent() && this.expirationTime.get().isBefore(expireAt);
    }

    public boolean tryExpiration(MemCache cache) {
        if (this.isAlive()) {
            return false;
        } else {
            _logger.info("Key expired: " + this.key + " and removed");
            cache.delete(this);
            return true;
        }
    }

    public void evict(MemCache cache) {
        _logger.info("Key evicted: " + this.key + " and removed");
        cache.delete(this);
    }

    public boolean tryAlive(MemCache cache) {
        return !this.tryExpiration(cache);
    }

    public boolean hasTTL() {
        return this.ttl.isPresent();
    }

    public boolean neverExpire() {
        return this.ttl.isEmpty();
    }
}
