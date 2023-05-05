package lb.strategies;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class LoadBalancerStrategy {
    static final Logger _logger = Logger.getLogger(LoadBalancerStrategy.class.getName());

    private List<String> servers;
    private List<String> validServers;
    private List<String> invalidServers;

    protected LoadBalancerStrategy() {
        this.servers = new ArrayList<>();
        this.validServers = new ArrayList<>();
        this.invalidServers = new ArrayList<>();
    }

    protected LoadBalancerStrategy(List<String> list) {
        this.servers = list;
        this.validServers = new ArrayList<>();
        this.invalidServers = new ArrayList<>();
    }

    public List<String> getServers() {
        return new ArrayList<>(this.validServers);
    }

    public void addServer(String url) {
        this.servers.add(url);
        this.invalidServers.add(url);
        this.validServers.remove(url);
    }

    public void removeServer(String url) {
        this.servers.remove(url);
        this.invalidServers.remove(url);
        this.validServers.remove(url);
    }

    public void healthy(String url) {
        this.invalidServers.remove(url);
        if (!this.validServers.contains(url)) {
            this.validServers.add(url);
            if (_logger.isLoggable(Level.INFO)) {
                _logger.info("+ Healthy backend: " + url);
            }
        } else {
            if (_logger.isLoggable(Level.FINEST)) {
                _logger.finest(" Still ok backend: " + url);
            }
        }
    }

    public void unhealthy(String url) {
        if (!this.invalidServers.contains(url)) {
            this.invalidServers.add(url);
        }
        if (validServers.contains(url)) {
            this.validServers.remove(url);
            if (_logger.isLoggable(Level.INFO)) {
                _logger.info("- Unhealthy backend: " + url);
            }
        }
    }

    public abstract Optional<String> getNext();

    public abstract Optional<String> getCurrent();

    public abstract void reset();

    public void startHealthChecks() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);

        // Define the piece of code to be executed
        Runnable runnable = () -> {
            var list = new ArrayList<>(this.servers);
            for (String server : list) {
                CompletableFuture.runAsync(() -> this.tryHealthCheck(server));
            }
        };
        executorService.scheduleAtFixedRate(runnable, 0, 2, TimeUnit.SECONDS);
    }

    public boolean tryHealthCheck(String url) {
        if (this.isHealthy(url)) {
            this.healthy(url);
            return true;
        } else {
            this.unhealthy(url);
            return false;
        }
    }

    private boolean isTcpHealthy(String urlString) {
        int timeout = 1000; // 1 seconds
        try (Socket socket = new Socket()) {
            var url = new URL(urlString);
            socket.connect(new InetSocketAddress(url.getHost(), url.getPort()), timeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isHealthy(String url) {
        if (!this.isTcpHealthy(url)) {
            return false;
        }
        try {
            var httpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(3, java.util.concurrent.TimeUnit.SECONDS)
                    .build();
            var request = new Request.Builder()
                    .url(url + "/health").get().build();
            var response = httpClient.newCall(request).execute();

            int statusCode = response.code();
            var responseBody = response.body().string();
            return statusCode == 200 && responseBody.startsWith("healthy");
        } catch (Exception e) {
            return false;
        }
    }
}
