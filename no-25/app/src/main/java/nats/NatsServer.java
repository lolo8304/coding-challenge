package nats;

import java.util.logging.Logger;

public class NatsServer {
    private static final Logger _logger = Logger.getLogger(NatsServer.class.getName());
    private int port;

    public NatsServer(int port) {
        this.port = port;
    }

    public void start() {
        _logger.info("Start server on port " + this.port);
    }
}
