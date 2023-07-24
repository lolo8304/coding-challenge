package memcached.commands;

import memcached.server.cache.MemCache;

public class DeleteCommand extends Command {
    private static CommandLine getCommandLine(String key) {
        return getCommandLine(key, false);
    }

    private static CommandLine getCommandLine(String key, boolean noreply) {
        return new CommandLine(
                String.format("delete %s %s", key, noreply ? "noreply" : ""));
    }

    public DeleteCommand(CommandLine commandLine) {
        super(commandLine);
    }

    public DeleteCommand(String... tokens) {
        super(tokens);
    }

    public DeleteCommand(String key) {
        super(getCommandLine(key));
    }

    public DeleteCommand(String key, boolean noreply) {
        super(getCommandLine(key, noreply));

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
