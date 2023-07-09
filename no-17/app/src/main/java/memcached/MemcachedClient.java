package memcached;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class MemcachedClient {
    static Logger _logger = Logger.getLogger(MemcachedClient.class.getName());

    private final String[] serverIds;
    private final Server[] servers;
    private final List<Server> validServers = new ArrayList<>();

    public MemcachedClient(String serverIds) {
        this.serverIds = Arrays.asList(serverIds.split(",")).stream().filter((x) -> !x.isEmpty() && !x.isBlank())
                .toArray(String[]::new);
        this.servers = initializeServers();
    }

    public MemcachedClient(String[] serverIds) {
        this.serverIds = serverIds;
        this.servers = initializeServers();
    }

    private Server[] initializeServers() {
        return Arrays.asList(this.serverIds).stream().map(Server::new).toArray(Server[]::new);
    }

    public boolean start() throws UnknownHostException, IOException {
        for (Server server : this.servers) {
            if (server.start()) {
                validServers.add(server);
            }
        }
        return !this.validServers.isEmpty();
    }

    public void stop() {
        this.validServers.forEach(Server::stop);
    }

    public Server[] getServers() {
        return this.servers;
    }

    public Server[] getValidServers() {
        return this.validServers.toArray(Server[]::new);
    }
}
