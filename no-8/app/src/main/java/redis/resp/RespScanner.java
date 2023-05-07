package redis.resp;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import redis.resp.commands.RespCommand;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespArray;
import redis.resp.types.RespBulkString;
import redis.resp.types.RespError;
import redis.resp.types.RespInteger;
import redis.resp.types.RespSimpleString;
import redis.resp.types.RespType;
import static redis.resp.types.RespNull.NULL;

public class RespScanner {

    private final ByteBuffer buffer;
    private int position;

    public static String convertNewLines(String str) {
        return str.replace("\\r", "\r").replace("\\n", "\n");
    }

    public static String convertNewLinesBack(String str) {
        return str.replace("\r", "\\r").replace("\n", "\\n");
    }

    public static RespScanner fromString(String str) {
        return new RespScanner(str);
    }

    public static RespScanner fromEscapedString(String str) {
        return fromString(convertNewLines(str));
    }

    public RespScanner(String str) {
        var strBuffer = ByteBuffer.wrap(str.getBytes());
        this.buffer = strBuffer;
        this.position = strBuffer.position();
    }

    public RespScanner(ByteBuffer buffer) {
        this.buffer = buffer;
        this.position = buffer.position();
    }

    public boolean hasNext() {
        return buffer.hasRemaining() && buffer.capacity() > position;
    }

    public Optional<RespType> next() throws RespException {
        try {
            byte type = buffer.get(position++);

            switch (type) {
                case '+': // Simple string
                    return readSimpleString();

                case '-': // Error message
                    return readError();

                case ':': // Integer
                    return readInteger();

                case '$': // Bulk string
                    return readBulkString();

                case '*': // Array
                    return readArray();

                default:
                    throw new RespException("Invalid RESP type: " + type);
            }
        } catch (RespException e) {
            throw e;
        } catch (Exception e) {
            throw new RespException(e.getMessage());
        }
    }

    private Optional<RespType> readSimpleString() {
        var value = readLine();
        if (value.isPresent()) {
            return Optional.of(new RespSimpleString(value.get()));
        } else {
            return Optional.empty();
        }
    }

    private Optional<RespType> readError() {
        var value = readLine();
        if (value.isPresent()) {
            return Optional.of(new RespError(value.get()));
        } else {
            return Optional.empty();
        }
    }

    private Optional<String> readLine() {
        int start = position;
        while (buffer.hasRemaining()) {
            byte b = buffer.get(position++);
            if (b == '\r' && buffer.hasRemaining() && buffer.get(position) == '\n') {
                int end = position - 1;
                position++;
                return Optional.of(new String(buffer.array(), start, end - start, StandardCharsets.UTF_8));
            }
        }
        position = start;
        return Optional.empty();
    }

    private Optional<RespType> readInteger() {
        var line = readLine();
        if (line.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new RespInteger(Long.parseLong(line.get())));
    }

    private Optional<RespType> readBulkString() throws RespException {
        var intValue = readInteger();
        if (intValue.isEmpty()) {
            return intValue;
        }
        var length = intValue.get().intValue();
        if (length < 0) {
            return Optional.of(NULL);
        }
        int start = position;
        position += length;
        if (buffer.hasRemaining() && buffer.get(position) == '\r' && buffer.hasRemaining()
                && buffer.get(position + 1) == '\n') {
            int end = position;
            position += 2;
            return Optional.of(
                    new RespBulkString(length, new String(buffer.array(), start, end - start, StandardCharsets.UTF_8)));
        }
        position = start;
        return Optional.empty();
    }

    private Optional<RespType> readArray() throws RespException {
        var intValue = readInteger();
        if (intValue.isEmpty()) {
            return intValue;
        }
        var length = intValue.get().intValue();
        if (length < 0) {
            return Optional.of(new RespArray(length, RespType.EMPTY_ARRAY));
        }
        var array = new RespType[length.intValue()];
        for (int i = 0; i < length; i++) {
            var next = next();
            if (next.isEmpty()) {
                throw new RespException("Error while parsingg array");
            } else {
                array[i] = next.get();
            }
        }
        return Optional.of(new RespArray(array));
    }

    public List<RespCommand> getCommands() throws RespCommandException {
        try {
            var list = new ArrayList<RespCommand>();
            while (hasNext()) {
                var respType = next();
                if (respType.isPresent()) {
                    if (respType.get().isCommandType()) {
                        list.add(new RespCommand((RespArray) respType.get()));
                    }
                }

            }
            return list;

        } catch (RespException e) {
            throw new RespCommandException("Command not read due to exception " + e.getMessage());
        }

    }
}
