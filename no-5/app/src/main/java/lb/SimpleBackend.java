package lb;

import java.io.IOException;
import java.io.PrintWriter;
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

public class SimpleBackend {

    static final Logger _logger = Logger.getLogger(SimpleBackend.class.getName());

    static void log(HttpServletRequest request) {
        _logger.info(
                request.getRemoteAddr() + " " +
                        request.getMethod() + " " + request.getRequestURI() + " " + request.getProtocol()
                        + " User-Agent: " + request.getHeader("User-Agent"));
    }

    public SimpleBackend(int port) throws Exception {

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
        context.addServlet(new ServletHolder(new BackendServlet()), "/"); // Map /hello URL to HelloServlet
        context.addServlet(new ServletHolder(new HealthServlet()), "/health"); // Map /hello URL to HelloServlet

        _logger.info("Start Backend on port " + port);
        // Start the server
        server.start();
        server.join();
    }

    public static class BackendServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            // Set the response content type
            response.setContentType("text/plain");

            // Write the response body
            PrintWriter writer = response.getWriter();
            writer.println("Replied with a hello message");
            writer.flush();
            writer.close();

            SimpleBackend.log(request);
        }
    }

    public static class HealthServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            // Set the response content type
            response.setContentType("text/plain");

            // Write the response body
            PrintWriter writer = response.getWriter();
            writer.println("healthy");
            writer.flush();
            writer.close();

            SimpleBackend.log(request);
        }
    }
}
