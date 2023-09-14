package nats.protocol;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Optional;

import nats.NatsServer;
import nats.listener.StringHandler;

public class NatsHandler extends StringHandler {

    private NatsParser parser;
    private NatsServer server;

    public NatsHandler(NatsServer server) {
        this.server = server;
        this.parser = new NatsParser();
    }

    @Override
    public Optional<String> accept(SocketChannel clientSocketChannel, SelectionKey key) throws IOException {
        return getNewInfo(clientSocketChannel).send();
    }

    private Info getNewInfo(SocketChannel clientSocketChannel) {
        return new Info(clientSocketChannel.hashCode(), this.server.port);
    }

    @Override
    public Optional<String> request(SocketChannel clientSocketChannel, String line) throws IOException {
        if (line.toLowerCase().equals("info")) {
            return this.getNewInfo(clientSocketChannel).send();
        }
        if (line.toLowerCase().equals("quit")) {
            this.deregisterBuffer(clientSocketChannel);
            clientSocketChannel.close();
            return Optional.empty();
        }
        if (line.toLowerCase().equals("shutdown")) {
            this.deregisterBuffer(clientSocketChannel);
            clientSocketChannel.close();
            this.server.stop();
            return Optional.empty();
        }

        var command = this.parser.parse(clientSocketChannel, line);
        if (command.isPresent()) {
            return command.get().execute();
        } else {
            return Optional.of("-E 'Command not found'" + ICmd.EOL);
        }
    }

}
