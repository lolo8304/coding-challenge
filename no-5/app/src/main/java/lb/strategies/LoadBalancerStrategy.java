package lb.strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public abstract class LoadBalancerStrategy {
    private List<String> servers;
    private List<String> validServers;
    private List<String> invalidServers;

    public LoadBalancerStrategy() {
        this.servers = new ArrayList<>();
        this.validServers = new ArrayList<>();
        this.invalidServers = new ArrayList<>();
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

    public void checkHealthChecks() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

        // Define the piece of code to be executed
        Runnable runnable = () -> {
            for (String server : servers) {
                if (!this.isHealthy(server)) {

                }
            }
        };
        executorService.scheduleAtFixedRate(runnable, 0, 10, TimeUnit.SECONDS);
    }

    private boolean isHealthy(String url) {
        try {
            OkHttpClient httpClient = new OkHttpClient();
            var request = new Request.Builder().url(url + "/health").get().build();
            var response = httpClient.newCall(request).execute();

            int statusCode = response.code();
            String responseBody = response.body().string();
            return statusCode == 200 && responseBody.equals("healthy");
        } catch (Exception e) {
            return false;
        }
    }
}
