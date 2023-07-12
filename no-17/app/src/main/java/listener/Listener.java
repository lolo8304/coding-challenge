package listener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.logging.Logger;

public class Listener {
    public static final Logger _logger = Logger.getLogger(Listener.class.getName());
    private IListenerHandler handler;

    public Listener(int port, IListenerHandler handler) throws InterruptedException {
        this.handler = handler;
        start(port);
    }

    private void start(int port) {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            _logger.info("Memcached server started on port " + port);

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                for (SelectionKey key : selectedKeys) {
                    try {
                        if (key.isAcceptable()) {
                            acceptConnection(key);
                        } else if (key.isReadable()) {
                            readCommand(key);
                        }
                    } catch (IOException e) {
                        var clientSocketChannel = (SocketChannel) key.channel();
                        _logger.info("Error: will close channel " + clientSocketChannel.getRemoteAddress());
                        try {
                            clientSocketChannel.close();
                        } catch (IOException ex) {
                            // ok if error on close
                        }
                    }
                }
                selectedKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptConnection(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        var clientSocketChannel = serverSocketChannel.accept();
        clientSocketChannel.configureBlocking(false);
        clientSocketChannel.register(key.selector(), SelectionKey.OP_READ);
        this.handler.registerBuffer(clientSocketChannel);
        _logger.info("New client connected: " + clientSocketChannel.getRemoteAddress());
    }

    private void readCommand(SelectionKey key) throws IOException {
        var clientSocketChannel = (SocketChannel) key.channel();
        if (clientSocketChannel.isOpen()) {
            this.handler.request(clientSocketChannel);
        }
    }
}
