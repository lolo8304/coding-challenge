package lb;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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

public class SimpleLoadBalancer {

    static final Logger _logger = Logger.getLogger(SimpleLoadBalancer.class.getName());
    private int port;
    private LoadBalancerStrategy lbStrategy;

    static void log(String be, HttpServletRequest request) {
        _logger.info(
                be + " " + request.getRemoteAddr() + " " +
                        request.getMethod() + " " + request.getRequestURI() + " " + request.getProtocol()
                        + " User-Agent: " + request.getHeader("User-Agent"));
    }

    public SimpleLoadBalancer(int port, LoadBalancerStrategy lbStrategy) throws Exception {
        this.port = port;
        this.lbStrategy = lbStrategy;

        // Create a Jetty server instance
        Server server = new Server(new QueuedThreadPool(20));
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port); // Set the desired port
        server.addConnector(connector);

        // Create a servlet context handler
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
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

        private SimpleLoadBalancer loadbalancer;

        public LoadBalancerServlet(SimpleLoadBalancer loadbalancer) {
            this.loadbalancer = loadbalancer;
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {

            var be = loadbalancer.lbStrategy.getNext();

            OkHttpClient httpClient = new OkHttpClient();
            var reqBuilder = new Request.Builder()
                    .url(be + request.getRequestURI());
            var reqHeaders = request.getHeaderNames();
            while (reqHeaders.hasMoreElements()) {
                String reqHeaderKey = reqHeaders.nextElement();
                String reqHeaderValue = request.getHeader(reqHeaderKey);
                reqBuilder.header(reqHeaderKey, reqHeaderValue);
            }
            var beRequest = reqBuilder.build();
            var beResponse = httpClient.newCall(beRequest).execute();

            int statusCode = beResponse.code();
            String responseBody = beResponse.body().string();
            response.setContentType(beResponse.header("Content-Type"));
            response.setStatus(statusCode);
            beResponse.close();

            PrintWriter writer = response.getWriter();
            writer.println("from " + be + " // " + responseBody);
            writer.flush();
            writer.close();

            SimpleLoadBalancer.log(be, request);
        }
    }
}
