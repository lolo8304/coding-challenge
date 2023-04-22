package lb;

import java.util.ArrayList;
import java.util.List;

public abstract class LoadBalancerStrategy {
    private List<String> servers;

    public LoadBalancerStrategy() {
        this.servers = new ArrayList<>();
    }

    public LoadBalancerStrategy(List<String> list) {
        this.servers = list;
    }

    public List<String> getServers() {
        return this.servers;
    }

    public void addServer(String url) {
        this.servers.add(url);
    }

    public void removeServer(String url) {
        this.servers.remove(url);
    }

    public abstract String getNext();

    public abstract String getCurrent();

    public abstract void reset();
}
