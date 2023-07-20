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
        return tmpkeys.stream().filter((x)-> !x.isBlank()).toArray(String[]::new);
    }

    @Override
    public void validate() throws ValidationException {
        if (this.keys == null || this.keys.length == 0) {
            throw new ValidationException("Key is mandatory, but null or empty");
        }
        if (this.commandLine == null) {
            throw new ValidationException("Commandline is empty");
        }
        if (this.type == null || this.type.isBlank()) {
            throw new ValidationException("Type is mandatory, but null or empty");
        }

    }

}
