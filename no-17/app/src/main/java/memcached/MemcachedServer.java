package memcached;

public class MemcachedServer {
    private String serverName;
    private int port;

    public MemcachedServer(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
    }

    public String getServerId() {
        return String.format("%s-%d", this.serverName, this.port);
    }

    public void start() {
    }
}
