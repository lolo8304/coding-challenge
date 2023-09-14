package nats;

import java.util.logging.Logger;

import nats.listener.Listener;
import nats.protocol.NatsHandler;

public class NatsServer {
    private static final Logger _logger = Logger.getLogger(NatsServer.class.getName());
    public final int port;
    private Listener listener;
    private NatsHandler handler;

    public NatsServer(int port) throws InterruptedException {
        this.port = port;
        this.handler = new NatsHandler(this);
        this.listener = new Listener(this.port, handler);

    }

    public void start() {
        _logger.info("Start server on port " + this.port);
        this.listener.start();
    }

    public void stop() {
        _logger.info("Shutdown server on port " + this.port);
        this.listener.stop();
    }
}
