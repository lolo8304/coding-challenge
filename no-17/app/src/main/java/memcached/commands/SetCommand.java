package memcached.commands;

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

}
