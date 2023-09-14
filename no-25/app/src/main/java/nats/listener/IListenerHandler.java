package nats.listener;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public interface IListenerHandler {
    public void request(SocketChannel clientSocketChannel) throws IOException;

    public StringBuilder registerBuffer(SocketChannel key);

    public void deregisterBuffer(SocketChannel key);

    public StringBuilder getBuffer(SocketChannel key);

    public void acceptConnection(SocketChannel clientSocketChannel, SelectionKey key) throws IOException;

    public void write(SocketChannel clientSocketChannel, String data) throws IOException;

}
