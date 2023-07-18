package memcached.commands;

import java.util.Optional;

import memcached.server.cache.CacheContext;

public class PrependCommand extends ReplaceCommand {
    private static CommandLine getCommandLine(String key, String value) {
        return getCommandLine(key, value, 0, 0, false);
    }

    private static CommandLine getCommandLine(String key, String value, int flags, int exptime, boolean noreply) {
        return new CommandLine(
                String.format("prepend %s %d %d %d %s", key, flags, exptime, value.length(), noreply ? "noreply" : ""));
    }

    public PrependCommand(CommandLine commandLine, Data data) {
        super(commandLine, data);
    }

    public PrependCommand(String... tokens) {
        super(tokens);
    }

    public PrependCommand(String key, String value) {
        super(getCommandLine(key, value), new Data(value));
    }

    public PrependCommand(String key, String value, int flags, int exptime, boolean noreply) {
        super(getCommandLine(key, value, flags, exptime, noreply), new Data(value));

    }

    @Override
    public Optional<ValidationCode> setToContext(CacheContext cacheContext) {
        // Append the data to the existing value
        cacheContext.command().prepend(this.data.data);
        return cacheContext.updateAndStatus(cacheContext.command());
    }
}
