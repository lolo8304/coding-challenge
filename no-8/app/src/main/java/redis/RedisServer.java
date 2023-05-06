package redis;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import jakarta.servlet.http.HttpServletRequest;

public class RedisServer {
    static final Logger _logger = Logger.getLogger(RedisServer.class.getName());

    private final ExecutorService executor;

    public RedisServer(int port, int maxThreads) {
        executor = Executors.newFixedThreadPool(maxThreads);
        start(port, maxThreads);
    }

    @SuppressWarnings("java:S2189")
    protected void start(int port, int maxThreads) {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            _logger.info("Redis server kistening on port " + port + " with #threads " + maxThreads);

            while (true) {
                Socket socket = serverSocket.accept();
                _logger.info("Accepted connection from " + socket.getInetAddress());

                executor.submit(() -> handleRequest(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void handleRequest(Socket socket) {
        try (InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);

            String request = new String(buffer, 0, bytesRead);
            _logger.info("Received request: " + request);

            outputStream.write(buffer, 0, bytesRead);
            outputStream.flush();

            _logger.info("Sent response: " + request);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
