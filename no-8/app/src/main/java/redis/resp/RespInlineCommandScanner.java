package redis.resp;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import redis.resp.commands.RespInlineCommand;

public class RespInlineCommandScanner {
    private final ByteBuffer buffer;

    public RespInlineCommandScanner(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public RespInlineCommandScanner(String str) {
        this.buffer = ByteBuffer.wrap(str.getBytes(StandardCharsets.UTF_8));
    }

    public Optional<List<String>> nextCommand() {
        List<String> commandArgs = new ArrayList<>();
        boolean inQuote = false;
        boolean escaped = false;
        StringBuilder argBuilder = new StringBuilder();

        while (buffer.hasRemaining()) {
            byte nextByte = buffer.get();

            if (nextByte == '"' && !escaped) {
                inQuote = !inQuote;
            } else if (nextByte == ' ' && !inQuote && !escaped) {
                commandArgs.add(argBuilder.toString());
                argBuilder = new StringBuilder();
            } else if (nextByte == '\\' && !escaped) {
                escaped = true;
            } else {
                argBuilder.append((char) nextByte);
                escaped = false;
            }

            if (buffer.remaining() == 0) {
                commandArgs.add(argBuilder.toString());
            }
        }

        if (commandArgs.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(commandArgs);
    }

    public Optional<RespInlineCommand> nextInlineCommand() {
        var next = nextCommand();
        if (next.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new RespInlineCommand(next.get()));
    }

}
