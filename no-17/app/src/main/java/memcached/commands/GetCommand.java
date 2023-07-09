package memcached.commands;

import java.util.ArrayList;
import java.util.Arrays;

public class GetCommand extends Command {

    public final String[] keys;

    private static CommandLine getCommandLine(String... keys) {
        return new CommandLine(String.format("get %s", String.join(" ", keys)));
    }

    public GetCommand(CommandLine commandLine) {
        super(commandLine);
        this.keys = this.keys();
    }

    public GetCommand(String... keys) {
        super(getCommandLine(keys));
        this.keys = this.keys();
    }

    public GetCommand(String key) {
        super(getCommandLine(key));
        this.keys = this.keys();
    }

    private String[] keys() {
        var tmpkeys = new ArrayList<String>();
        tmpkeys.add(key);
        tmpkeys.addAll(Arrays.asList(this.parameters));
        return tmpkeys.toArray(String[]::new);
    }

}
