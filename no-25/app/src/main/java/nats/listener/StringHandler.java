package nats.listener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import nats.protocol.commands.ICmd;

public abstract class StringHandler implements IListenerHandler {
    public static final Logger _logger = Logger.getLogger(StringHandler.class.getName());

    private static final int BUFFER_SIZE = 1024;

    private Map<SocketChannel, StringBuilder> lineBuffer;

    protected StringHandler() {
        this.lineBuffer = new HashMap<>();
    }

    public StringBuilder registerBuffer(SocketChannel channel) {
        var newBuilder = new StringBuilder();
        this.lineBuffer.put(channel, newBuilder);
        return newBuilder;
    }

    public void deregisterBuffer(SocketChannel channel) {
        if (this.lineBuffer.containsKey(channel)) {
            this.lineBuffer.remove(channel);
        }
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
        var nextCRLF = buffer.indexOf(ICmd.CRLF);
        if (nextCRLF == -1) {
            line = buffer.toString();
            buffer.setLength(0);
        } else {
            line = buffer.substring(0, nextCRLF + 2);
            buffer.delete(0, nextCRLF + 2);
        }
        return line;
    }

    @Override
    public void request(SocketChannel clientSocketChannel) throws IOException {
        var line = readLineFromSocketChannel(clientSocketChannel);
        if (line != null && !line.isEmpty()) {
            this.log(true, clientSocketChannel.hashCode(), line);
            var response = this.request(clientSocketChannel, line);
            if (response.isPresent()) {
                this.log(false, clientSocketChannel.hashCode(), response.get());
                this.write(clientSocketChannel, response.get());
            } else {
                this.log(false, clientSocketChannel.hashCode(), null);
            }
        }
    }

    private void log(boolean inbound, int cid, String toLog) {
        var direction = inbound ? "<<-" : "->>";
        if (toLog != null) {
            toLog = toLog.endsWith(ICmd.CRLF) ? toLog.substring(0, toLog.length() - 2) : toLog;
            _logger.info(String.format("cid:%d - %s [%s]", cid, direction, toLog));
        } else {
            _logger.info(String.format("cid:%d - %s (none)", cid, direction));
        }
    }

    public abstract Optional<String> request(SocketChannel clientSocketChannel, String line) throws IOException;

    public abstract Optional<String> accept(SocketChannel clientSocketChannel, SelectionKey key)
            throws IOException;

    @Override
    public void acceptConnection(SocketChannel clientSocketChannel, SelectionKey key) throws IOException {
        var response = this.accept(clientSocketChannel, key);
        if (response.isPresent()) {
            this.log(false, clientSocketChannel.hashCode(), response.get());
            this.write(clientSocketChannel, response.get());
        }
    }

    @Override
    public void write(SocketChannel clientSocketChannel, String data) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8));
        clientSocketChannel.write(byteBuffer);
    }
}
