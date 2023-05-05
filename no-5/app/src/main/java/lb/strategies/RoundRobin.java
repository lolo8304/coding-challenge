package lb.strategies;

import java.util.List;
import java.util.Optional;

public class RoundRobin extends LoadBalancerStrategy {
    private static Object syncObject = new Object();

    private Optional<String> currentUrl;

    public RoundRobin(List<String> list) {
        super(list);
        this.currentUrl = Optional.empty();
    }

    private Optional<String> findNext(Optional<String> lastUrl, List<String> list) {
        if (list.isEmpty()) {
            return Optional.empty();
        }
        if (lastUrl.isEmpty()) {
            return Optional.of(list.get(0));
        }
        var i = 0;
        while (i < list.size()) {
            if (list.get(i) != null && list.get(i).equals(lastUrl.get())) {
                return Optional.of(list.get((i + 1) % list.size()));
            }
            i++;
        }
        return Optional.of(list.get(0));
    }

    @Override
    public Optional<String> getNext() {
        synchronized (syncObject) {
            this.currentUrl = this.findNext(this.currentUrl, getServers());
            return this.currentUrl;
        }
    }

    @Override
    public Optional<String> getCurrent() {
        synchronized (syncObject) {
            return this.currentUrl;
        }
    }

    @Override
    public void reset() {
        synchronized (syncObject) {
            this.currentUrl = Optional.empty();
        }
    }

}
