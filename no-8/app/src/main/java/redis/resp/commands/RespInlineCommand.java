package redis.resp.commands;

import java.util.Arrays;
import java.util.List;

import redis.resp.RespInlineCommandScanner;
import redis.resp.types.RespArray;
import redis.resp.types.RespBulkString;
import redis.resp.types.RespInteger;
import redis.resp.types.RespType;

public class RespInlineCommand {
    private static final String[] EMPTY = new String[0];

    public final String[] commands;

    public RespInlineCommand(String inlineCommand) {
        var scanner = new RespInlineCommandScanner(inlineCommand);
        var command = scanner.nextCommand();
        if (command.isPresent()) {
            this.commands = command.get().toArray(String[]::new);
        } else {
            this.commands = EMPTY;
        }
    }

    public RespInlineCommand(String[] commands) {
        this.commands = commands;
    }

    public RespInlineCommand(List<String> commands) {
        this.commands = commands.toArray(String[]::new);
    }

    public RespCommand toCommand() {
        RespType[] bulkStrings = Arrays.asList(commands).stream().map(x -> this.toRespType(x))
                .toArray(RespType[]::new);
        var buldArray = new RespArray(bulkStrings.length, bulkStrings);
        return new RespCommand(buldArray);
    }

    private RespType toRespType(String str) {
        String regex = "^-?\\d+$";
        if (str.matches(regex)) {
            int value = Integer.parseInt(str);
            return new RespInteger(value);
        } else {
            return new RespBulkString(str.length(), str);
        }
    }

    public int size() {
        return commands.length;
    }
}
