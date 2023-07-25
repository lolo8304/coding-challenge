package memcached.commands;

import java.util.Optional;

import memcached.server.cache.CacheContext;
import memcached.server.cache.MemCache;

public class DecrCommand extends SetCommand {
    private static CommandLine getCommandLine(String key, String value) {
        return getCommandLine(key, value, false);
    }

    private static CommandLine getCommandLine(String key, String value, boolean noreply) {
        return new CommandLine(
                String.format("decr %s %s %s", key, value, noreply ? "noreply" : ""));
    }

    public DecrCommand(CommandLine commandLine) {
        super(commandLine, null);
    }

    public DecrCommand(String key, String value) {
        super(getCommandLine(key, value), null);
    }

    public DecrCommand(String key, String value, boolean noreply) {
        super(getCommandLine(key, value, noreply), null);
    }

    @Override
    public ValidationCode isValidToAddToCache(MemCache cache) {
        // allow add only if key already exists in cache
        if (cache.get(this.key).isPresent()) {
            return ValidationCode.OK;
        } else {
            return ValidationCode.NOT_FOUND;
        }
    }

    @Override
    public Optional<ValidationCode> setToContext(CacheContext cacheContext) {
        // Append the data to the existing value
        try {
            var decrValue = this.parameter0();
            if (decrValue.isPresent()) {
                cacheContext.command().decr(decrValue.get());
                return cacheContext.updateAndStatus(cacheContext.command());
            } else {
                return Optional.of(ValidationCode.ERROR);
            }

        } catch (NumberFormatException e) {
            return Optional.of(ValidationCode.CLIENT_ERROR_CANNOT_INCREMENT_OR_DECREMENT_NON_NUMERIC_VALUE);
        }
    }

}
