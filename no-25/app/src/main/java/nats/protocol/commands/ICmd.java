package nats.protocol.commands;

import java.io.IOException;
import java.util.Optional;

import nats.runtime.NatsContext;

public interface ICmd {
    public static final String CRLF = "" + '\r' + '\n';

    public Optional<String> print() throws IOException;

    public Optional<String> executeCommand(NatsContext context) throws IOException;
}
