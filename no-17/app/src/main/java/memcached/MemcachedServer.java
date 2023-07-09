package memcached;

import listener.Listener;
import memcached.listener.MemcachedHandler;

public class MemcachedServer {
    private String serverName;
    private int port;
    private Listener listener;

    public MemcachedServer(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
    }

    public String getServerId() {
        return String.format("%s-%d", this.serverName, this.port);
    }

    public void start() throws InterruptedException {
        var handler = new MemcachedHandler();
        this.listener = new Listener(this.port, handler);
    }

}
