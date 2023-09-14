package nats.protocol;

import java.nio.channels.SocketChannel;
import java.util.Optional;

public class NatsParser {

    public NatsParser() {
    }

    public Optional<ICmd> parse(SocketChannel clientSocketChannel, String line) {
        if (line.startsWith("connect {")) {
            return Optional.of(new Connect().buildVerbose());
        }
        return Optional.empty();
    }

}
