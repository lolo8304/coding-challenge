package listener;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface IListenerHandler {
    public void request(SocketChannel clientSocketChannel) throws IOException;

    public StringBuilder registerBuffer(SocketChannel key);

    public StringBuilder getBuffer(SocketChannel key);

}
