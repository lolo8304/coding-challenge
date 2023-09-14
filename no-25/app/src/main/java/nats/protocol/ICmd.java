package nats.protocol;

import java.io.IOException;
import java.util.Optional;

public interface ICmd {
    public static final String EOL = "" + '\r' + '\n';

    public Optional<String> send() throws IOException;

    public Optional<String> execute() throws IOException;
}
