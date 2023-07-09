package memcached.commands;

import java.util.Arrays;

public class CommandLine {
    public final String line;

    public CommandLine(String[] tokens) {
        this(String.join(" ", tokens));
    }

    public CommandLine(String line) {
        this.line = line;

    }

    public String[] getTokens() {
        return Arrays.asList(this.line.split(" ")).stream().filter((x) -> !x.isBlank()).toArray(String[]::new);
    }

    public Command asCommand() {
        return new Command(this, this.getTokens());
    }

    public Command asDataCommand() {
        return new DataCommand(this, this.getTokens());
    }

}
