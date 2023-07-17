package memcached.commands;

import memcached.server.cache.MemCache;

public class CasCommand extends ReplaceCommand {
    private static CommandLine getCasCommandLine(String key, String value, int cas) {
        return getCasCommandLine(key, value, 0, 0, cas, false);
    }

    private static CommandLine getCasCommandLine(String key, String value, int flags, int exptime, int cas,
            boolean noreply) {
        return new CommandLine(
                String.format("cas %s %d %d %d %d %s", key, flags, exptime, value.length(), cas,
                        noreply ? "noreply" : ""));
    }

    public CasCommand(CommandLine commandLine, Data data) {
        super(commandLine, data);
    }

    public CasCommand(String... tokens) {
        super(tokens);
    }

    public CasCommand(String key, String value, int cas) {
        super(getCasCommandLine(key, value, cas), new Data(value));
    }

    public CasCommand(String key, String value, int flags, int exptime, int cas, boolean noreply) {
        super(getCasCommandLine(key, value, flags, exptime, cas, noreply), new Data(value));

    }

    public int cas() {
        var e = this.parameterInt(3);
        if (e.isPresent()) {
            return e.get();
        } else {
            return 0;
        }
    }

    @Override
    public ValidationCode isValidToAddToCache(MemCache cache) {
        // allow add only if key already exists in cache
        var context = cache.get(this.key);
        if (context.isPresent()) {
            context = cache.get(this.key, this.cas());
            return (context.isPresent()) ? ValidationCode.OK : ValidationCode.EXISTS;
        } else {
            return ValidationCode.NOT_FOUND;
        }
    }
}
