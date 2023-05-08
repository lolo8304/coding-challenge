package redis.resp;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import redis.resp.commands.RespInlineCommand;

public class RespPipelineInlineScanner {
    private final ByteBuffer buffer;

    public RespPipelineInlineScanner(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public RespPipelineInlineScanner(String str) {
        this.buffer = ByteBuffer.wrap(str.getBytes());
    }

    public Optional<String> nextCommand() {
        StringBuilder commandBuilder = new StringBuilder();

        while (buffer.hasRemaining()) {
            byte nextByte = buffer.get();

            if (nextByte == '\r' && buffer.hasRemaining() && buffer.get() == '\n') {
                return Optional.of(commandBuilder.toString());
            }

            commandBuilder.append((char) nextByte);
        }

        var command = commandBuilder.toString();
        if (command.length() > 0) {
            return Optional.of(command);
        } else {
            return Optional.empty();
        }
    }

    public List<RespInlineCommand> getCommands() {
        Optional<String> command;
        var commands = new ArrayList<RespInlineCommand>();
        while ((command = this.nextCommand()).isPresent()) {
            commands.add(new RespInlineCommand(command.get()));
        }
        return commands;
    }
}
