package memcached.commands;

import memcached.server.cache.MemCache;

public class AddCommand extends SetCommand {
    private static CommandLine getCommandLine(String key, String value) {
        return getCommandLine(key, value, 0, 0, false);
    }

    private static CommandLine getCommandLine(String key, String value, int flags, int exptime, boolean noreply) {
        return new CommandLine(
                String.format("add %s %d %d %d %s", key, flags, exptime, value.length(), noreply ? "noreply" : ""));
    }

    public AddCommand(CommandLine commandLine, Data data) {
        super(commandLine, data);
    }

    public AddCommand(String... tokens) {
        super(tokens);
    }

    public AddCommand(String key, String value) {
        super(getCommandLine(key, value), new Data(value));
    }

    public AddCommand(String key, String value, int flags, int exptime, boolean noreply) {
        super(getCommandLine(key, value, flags, exptime, noreply), new Data(value));

    }

    @Override
    public boolean isValidToAddToCache(MemCache cache) {
        // allow add only if key does not exists in cache
        return (cache.get(this.key).isEmpty());
    }
}
