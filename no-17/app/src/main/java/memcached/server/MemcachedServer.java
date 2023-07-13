package memcached.server;

import listener.Listener;
import memcached.server.handler.MemcachedHandler;

public class MemcachedServer {
    private String serverName;
    private int port;
    private Listener listener;

    public MemcachedServer(String serverName, int port) throws InterruptedException {
        this.serverName = serverName;
        this.port = port;
        var handler = new MemcachedHandler();
        this.listener = new Listener(this.port, handler);
    }

    public String getServerId() {
        return String.format("%s-%d", this.serverName, this.port);
    }

    public void start() {
        this.listener.start();
    }

}
