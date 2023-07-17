package memcached.commands;

import memcached.server.cache.MemCache;

public class ReplaceCommand extends SetCommand {
    private static CommandLine getCommandLine(String key, String value) {
        return getCommandLine(key, value, 0, 0, false);
    }

    private static CommandLine getCommandLine(String key, String value, int flags, int exptime, boolean noreply) {
        return new CommandLine(
                String.format("replace %s %d %d %d %s", key, flags, exptime, value.length(), noreply ? "noreply" : ""));
    }

    public ReplaceCommand(CommandLine commandLine, Data data) {
        super(commandLine, data);
    }

    public ReplaceCommand(String... tokens) {
        super(tokens);
    }

    public ReplaceCommand(String key, String value) {
        super(getCommandLine(key, value), new Data(value));
    }

    public ReplaceCommand(String key, String value, int flags, int exptime, boolean noreply) {
        super(getCommandLine(key, value, flags, exptime, noreply), new Data(value));

    }

    @Override
    public ValidationCode isValidToAddToCache(MemCache cache) {
        // allow add only if key already exists in cache
        if (cache.get(this.key).isPresent()) {
            return ValidationCode.OK;
        } else {
            return ValidationCode.NOT_STORED;
        }
    }
}
