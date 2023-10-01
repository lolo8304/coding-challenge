package nats.listener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Listener {
    public static final Logger _logger = Logger.getLogger(Listener.class.getName());
    private IListenerHandler handler;
    private int port;
    private boolean started = false;

    public Listener(int port, IListenerHandler handler) throws InterruptedException {
        this.port = port;
        this.handler = handler;
    }

    public void start() {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            if (_logger.isLoggable(Level.INFO))
                _logger.info("Server started on port " + port);
            this.started = true;

            while (this.started) {
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
                        this.handler.deregisterBuffer(clientSocketChannel);
                        try {
                            clientSocketChannel.close();
                        } catch (IOException ex) {
                            // ok if error on close
                        }
                    }
                }
                selectedKeys.clear();
            }
            serverSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        this.started = false;
    }

    private void acceptConnection(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        var clientSocketChannel = serverSocketChannel.accept();
        clientSocketChannel.configureBlocking(false);
        clientSocketChannel.register(key.selector(), SelectionKey.OP_READ);
        this.handler.acceptConnection(clientSocketChannel, key);
        this.handler.registerBuffer(clientSocketChannel);
        if (_logger.isLoggable(Level.INFO))
            _logger.info("New client connected: " + clientSocketChannel.getRemoteAddress());
    }

    private void readCommand(SelectionKey key) throws IOException {
        var clientSocketChannel = (SocketChannel) key.channel();
        if (clientSocketChannel.isOpen()) {
            this.handler.request(clientSocketChannel);
        }
    }
}
