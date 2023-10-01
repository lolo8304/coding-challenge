package nats.protocol.commands;

import java.io.IOException;
import java.util.Optional;

import nats.protocol.NatsLineParser;
import nats.runtime.NatsContext;

public class Err implements ICmd {

    private String message;

    public Err() {
    }

    public Err(String message) {
        this.message = message;
    }

    public Err(NatsLineParser line) {
        this.message = line.nextToken().get().toString();
    }

    @Override
    public Optional<String> print() throws IOException {
        return Optional.of(String.format("ERR %s%s", this.message, CRLF));
    }

    @Override
    public Optional<String> executeCommand(NatsContext context) throws IOException {
        return Optional.empty();
    }

}
