package listener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class StringHandler implements IListenerHandler {
    private static final int BUFFER_SIZE = 1024;
    private static final String NEWLINE = "" + '\n';

    private Map<SocketChannel, StringBuilder> lineBuffer;

    protected StringHandler() {
        this.lineBuffer = new HashMap<>();
    }

    public StringBuilder registerBuffer(SocketChannel channel) {
        var newBuilder = new StringBuilder();
        this.lineBuffer.put(channel, newBuilder);
        return newBuilder;
    }

    public StringBuilder getBuffer(SocketChannel channel) {
        return this.lineBuffer.get(channel);
    }

    public String readLineFromSocketChannel(SocketChannel clientSocketChannel) throws IOException {
        var buffer = this.getBuffer(clientSocketChannel);
        if (buffer.length() == 0) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (clientSocketChannel.read(byteBuffer) == 0) {
                // wait until filled
            }
            var newString = "";
            do {
                byteBuffer.flip();
                byte[] bytesArray = new byte[byteBuffer.limit()];
                byteBuffer.get(bytesArray);
                newString = new String(bytesArray);
                buffer.append(newString);
                byteBuffer.clear();
            } while (clientSocketChannel.read(byteBuffer) > 0);
        }

        String line;
        var nextNewLine = buffer.indexOf(NEWLINE);
        if (nextNewLine == -1) {
            line = buffer.toString();
            buffer.setLength(0);
        } else {
            line = buffer.substring(0, nextNewLine + 1);
            buffer.delete(0, nextNewLine + 1);
        }
        return line.trim();
    }

    @Override
    public void request(SocketChannel clientSocketChannel) throws IOException {
        var line = readLineFromSocketChannel(clientSocketChannel);
        var response = this.request(clientSocketChannel, line);
        if (response.isPresent()) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(response.get().getBytes(StandardCharsets.UTF_8));
            clientSocketChannel.write(byteBuffer);
        }
    }

    public abstract Optional<String> request(SocketChannel clientSocketChannel, String line) throws IOException;

}
