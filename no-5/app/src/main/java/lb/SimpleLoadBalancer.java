package lb;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SimpleLoadBalancer {

    static final Logger _logger = Logger.getLogger(SimpleLoadBalancer.class.getName());

    static void log(HttpServletRequest request) {
        _logger.info(
                request.getRemoteAddr() + " " +
                        request.getMethod() + " " + request.getRequestURI() + " " + request.getProtocol()
                        + " User-Agent: " + request.getHeader("User-Agent"));
    }

    public SimpleLoadBalancer(int port) throws Exception {

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
        context.addServlet(new ServletHolder(new HelloServlet(this)), "/"); // Map /hello URL to HelloServlet

        _logger.info("Start Loadbalancer on port " + port);
        // Start the server
        server.start();
        server.join();
    }

    private String getBackendServer() {
        return "http://localhost:9000";
    }

    public static class HelloServlet extends HttpServlet {

        private SimpleLoadBalancer loadbalancer;

        public HelloServlet(SimpleLoadBalancer loadbalancer) {
            this.loadbalancer = loadbalancer;
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {

            try {

                HttpClient httpClient = HttpClientBuilder.create().build();

                var beRequest = new HttpGet(loadbalancer.getBackendServer() + request.getRequestURI());
                _logger.info("be call: " + loadbalancer.getBackendServer() + request.getRequestURI());
                _logger.info("be method: " + request.getMethod());

                var reqHeaders = request.getHeaderNames();
                while (reqHeaders.hasMoreElements()) {
                    String reqHeaderKey = reqHeaders.nextElement();
                    _logger.info("Set header " + reqHeaderKey + "=" +
                            request.getHeader(reqHeaderKey));
                    // beRequest.setHeader(reqHeaderKey, request.getHeader(reqHeaderKey));
                }
                _logger.info("be call: before execute");
                HttpResponse beResponse = httpClient.execute(beRequest);
                _logger.info("be call: executed");
                int statusCode = beResponse.getStatusLine().getStatusCode();
                _logger.info("be call: status received " + statusCode);

                HttpEntity httpEntity = beResponse.getEntity();
                String responseBody = EntityUtils.toString(httpEntity);

                response.setContentType(beResponse.getHeaders("Content-Type")[0].getValue());
                response.setStatus(statusCode);

                PrintWriter writer = response.getWriter();
                writer.print(responseBody);
                writer.flush();
                writer.close();

                SimpleLoadBalancer.log(request);

            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException("Error while sending to backend", e);
            }
        }
    }
}
