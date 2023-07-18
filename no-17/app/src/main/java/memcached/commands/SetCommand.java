package memcached.commands;

import java.util.Optional;

import memcached.server.cache.CacheContext;

public class SetCommand extends DataCommand {

    private static CommandLine getCommandLine(String key, String value) {
        return getCommandLine(key, value, 0, 0, false);
    }

    private static CommandLine getCommandLine(String key, String value, int flags, int exptime, boolean noreply) {
        return new CommandLine(
                String.format("set %s %d %d %d %s", key, flags, exptime, value.length(), noreply ? "noreply" : ""));
    }

    public SetCommand(CommandLine commandLine, Data data) {
        super(commandLine, data);
    }

    public SetCommand(String... tokens) {
        super(tokens);
    }

    public SetCommand(String key, String value) {
        super(getCommandLine(key, value), new Data(value));
    }

    public SetCommand(String key, String value, int flags, int exptime, boolean noreply) {
        super(getCommandLine(key, value, flags, exptime, noreply), new Data(value));

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

    public Optional<ValidationCode> setToContext(CacheContext cacheContext) {
        return cacheContext.updateAndStatus(this);
    }

}
