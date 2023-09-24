package nats;

import java.util.logging.Level;
import java.util.logging.Logger;

import nats.listener.Listener;
import nats.protocol.NatsHandler;

public class NatsServer {
    private static final Logger _logger = Logger.getLogger(NatsServer.class.getName());
    public final int port;
    private Listener listener;
    private NatsHandler handler;
    private NatsRuntime runtime;

    public NatsServer(int port) throws InterruptedException {
        this.port = port;
        this.runtime = new NatsRuntime();
        this.handler = new NatsHandler(this, this.runtime);
        this.listener = new Listener(this.port, handler);
    }

    public void start() {
        if (_logger.isLoggable(Level.INFO))
            _logger.info("Start server on port " + this.port);
        this.listener.start();
    }

    public void stop() {
        if (_logger.isLoggable(Level.INFO))
            _logger.info("Shutdown server on port " + this.port);
        this.listener.stop();
    }
}
