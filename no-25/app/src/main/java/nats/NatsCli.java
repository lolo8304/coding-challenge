package nats;

import java.util.logging.Logger;

public class NatsCli {
    private static final Logger _logger = Logger.getLogger(NatsCli.class.getName());

    private String hostname;
    private int port;

    public NatsCli(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void command(String command) {
        _logger.info(String.format("%s:%d> %s", this.hostname, this.port, command));
    }

}
