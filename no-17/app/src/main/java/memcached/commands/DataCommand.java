package memcached.commands;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;

public class DataCommand extends Command {

    public final Data data;

    public static Command parse(String line, String data) {
        return new DataCommand(new CommandLine(line), new Data(data));
    }

    public DataCommand(CommandLine commandLine, Data data) {
        super(commandLine);
        this.data = data;
    }

    public DataCommand(CommandLine commandLine, String... tokens) {
        super(commandLine, Arrays.copyOfRange(tokens, 0, tokens.length - 1));
        this.data = new Data(tokens[tokens.length - 1]);
    }

    public DataCommand(String... tokens) {
        super(Arrays.copyOfRange(tokens, 0, tokens.length - 1));
        this.data = new Data(tokens[tokens.length - 1]);
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        super.write(writer);
        if (data != null) {
            writer.write(data.data + '\r' + '\n');
        }
    }

    public int length() {
        return this.data.data.length();
    }

    public String lengthToken() {
        return String.valueOf(this.length());
    }

    public DataCommand asValueCommand() {
        return new DataCommand("VALUE", this.key, String.valueOf(this.flags()), this.lengthToken(), this.data.data);
    }

    @Override
    public String toResponseString() {
        var buffer = new StringBuilder();
        buffer.append(this.commandLine.line).append('\r').append('\n');
        buffer.append(this.data.data).append('\r').append('\n');
        return buffer.toString();
    }

    public SetCommand asSetCommand() {
        switch (this.type) {
            case "set":
                return new SetCommand(this.commandLine, this.data);
            case "replace":
                return new ReplaceCommand(this.commandLine, this.data);
            case "add":
                return new AddCommand(this.commandLine, this.data);
            case "cas":
                return new CasCommand(this.commandLine, this.data);

            default:
                throw new IllegalArgumentException(String.format("type '%s' is not valid set command", this.type));
        }
    }

}
