package lb;

import java.util.List;

public class RoundRobin extends LoadBalancerStrategy {
    private static Object SYNC = new Object();

    private int index;

    public RoundRobin(List<String> list) {
        super(list);
        this.index = 0;
    }

    @Override
    public String getNext() {
        synchronized (SYNC) {
            this.index = (this.index + 1) % this.getServers().size();
            return this.getServers().get(this.index);
        }
    }

    @Override
    public String getCurrent() {
        synchronized (SYNC) {
            return this.getServers().get(this.index);
        }
    }

    @Override
    public void reset() {
        synchronized (SYNC) {
            this.index = 0;
        }
    }

}
