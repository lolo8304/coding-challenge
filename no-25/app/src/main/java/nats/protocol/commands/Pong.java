package nats.protocol.commands;

import java.io.IOException;
import java.util.Optional;

import nats.protocol.NatsLineParser;
import nats.runtime.NatsContext;

public class Pong implements ICmd {

    public Pong() {
    }

    public Pong(NatsLineParser line) {
    }

    @Override
    public Optional<String> print() throws IOException {
        return Optional.of("PONG" + CRLF);
    }

    @Override
    public Optional<String> executeCommand(NatsContext context) throws IOException {
        return Optional.empty();
    }

}
