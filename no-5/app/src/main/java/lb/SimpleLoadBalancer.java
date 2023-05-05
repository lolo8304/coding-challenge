package lb;

import java.io.IOException;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lb.strategies.LoadBalancerStrategy;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SimpleLoadBalancer {

    static final Logger _logger = Logger.getLogger(SimpleLoadBalancer.class.getName());
    private LoadBalancerStrategy lbStrategy;

    static void log(String be, HttpServletRequest request) {
        _logger.info(
                be + " " + request.getRemoteAddr() + " " +
                        request.getMethod() + " " + request.getRequestURI() + " " + request.getProtocol()
                        + " User-Agent: " + request.getHeader("User-Agent"));
    }

    protected SimpleLoadBalancer(int port, LoadBalancerStrategy lbStrategy) throws Exception {
        this.lbStrategy = lbStrategy;

        // Create a Jetty server instance
        var server = new Server(new QueuedThreadPool(20));
        var connector = new ServerConnector(server);
        connector.setPort(port); // Set the desired port
        server.addConnector(connector);

        // Create a servlet context handler
        var context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Add a servlet to handle incoming requests
        context.addServlet(new ServletHolder(new LoadBalancerServlet(this)), "/"); // Map /hello URL to HelloServlet

        _logger.info("Start Loadbalancer on port " + port);
        // Start the server
        server.start();
        server.join();
    }

    public static class LoadBalancerServlet extends HttpServlet {

        private transient SimpleLoadBalancer loadbalancer;

        public LoadBalancerServlet(SimpleLoadBalancer loadbalancer) {
            this.loadbalancer = loadbalancer;
        }

        private Response executeRequest(String beUrl, HttpServletRequest request) throws IOException {
            var httpClient = new OkHttpClient();
            var reqBuilder = new Request.Builder()
                    .url(beUrl + request.getRequestURI());
            var reqHeaders = request.getHeaderNames();
            while (reqHeaders.hasMoreElements()) {
                var reqHeaderKey = reqHeaders.nextElement();
                var reqHeaderValue = request.getHeader(reqHeaderKey);
                reqBuilder.header(reqHeaderKey, reqHeaderValue);
            }
            var beRequest = reqBuilder.build();
            return httpClient.newCall(beRequest).execute();
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {

            var be = loadbalancer.lbStrategy.getNext();
            if (be.isEmpty()) {
                throw new ServletException("No backend found");
            }
            var beUrl = be.get();
            var retryCount = 1;
            while (retryCount > 0) {
                try {
                    var beResponse = this.executeRequest(beUrl, request);
                    int statusCode = beResponse.code();
                    var responseBody = beResponse.body().string();
                    response.setContentType(beResponse.header("Content-Type"));
                    response.setStatus(statusCode);
                    beResponse.close();

                    var writer = response.getWriter();
                    writer.println("from " + beUrl + " // " + responseBody);
                    writer.flush();
                    writer.close();

                    SimpleLoadBalancer.log(beUrl, request);
                    retryCount = 0;

                } catch (IOException e) {
                    loadbalancer.lbStrategy.unhealthy(beUrl);
                    retryCount--;
                }
            }
        }
    }
}
