package nats.protocol.commands;

import java.io.IOException;
import java.util.Optional;

import nats.protocol.NatsLineParser;
import nats.runtime.NatsContext;

public class Ping implements ICmd {

    public Ping() {
    }

    public Ping(NatsLineParser line) {
    }

    @Override
    public Optional<String> print() throws IOException {
        return Optional.of("PING" + CRLF);
    }

    @Override
    public Optional<String> executeCommand(NatsContext context) throws IOException {
        return new Pong().print();
    }

}
