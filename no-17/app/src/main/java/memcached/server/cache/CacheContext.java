package memcached.server.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.logging.Logger;

import memcached.commands.SetCommand;

public class CacheContext {
    private static final Logger _logger = Logger.getLogger(CacheContext.class.getName());
    private MemCache cache;
    private SetCommand command;
    private Optional<Duration> ttl;
    private Optional<Instant> expirationTime;

    public CacheContext(MemCache cache, SetCommand command) {
        this.cache = cache;
        this.command = command;
        initTtl();
    }

    private void initTtl() {
        var expInS = this.command.exptime();
        if (expInS != 0) {
            this.ttl = Optional.of(ExpireIn.seconds(expInS).duration);
            this.expirationTime = Optional.of(Instant.now().plus(this.ttl.get()));
        } else {
            this.ttl = Optional.empty();
            this.expirationTime = Optional.empty();
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

    public boolean tryExpiration() {
        if (this.isAlive()) {
            return false;
        } else {
            _logger.info("Key expired: " + this.command.key + " and removed");
            this.cache.delete(this.command);
            return true;
        }
    }

    public void evict() {
        _logger.info("Key evicted: " + this.command.key + " and removed");
        cache.delete(this.command);
    }

    public boolean tryAlive() {
        return !this.tryExpiration();
    }

    public boolean hasTTL() {
        return this.ttl.isPresent();
    }

    public boolean neverExpire() {
        return this.ttl.isEmpty();
    }

    public SetCommand command() {
        return this.command;
    }
}
