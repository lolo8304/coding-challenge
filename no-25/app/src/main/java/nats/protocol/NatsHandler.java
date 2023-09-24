package nats.protocol;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Optional;

import nats.NatsServer;
import nats.listener.StringHandler;
import nats.protocol.commands.ICmd;
import nats.protocol.commands.Info;
import nats.runtime.NatsRuntime;

public class NatsHandler extends StringHandler {

    private static final String SHUTDOWN_CMD = "shutdown" + ICmd.CRLF;
    private static final String QUIT_CMD = "quit" + ICmd.CRLF;
    private static final String INFO_CMD = "info" + ICmd.CRLF;
    private static final String CTRLC_CMD = "" + (char) 65533 + (char) 65533 + (char) 65533 + (char) 65533
            + (char) 6;
    private final NatsParser parser;
    private final NatsServer server;
    private final NatsRuntime runtime;

    public NatsHandler(NatsServer server, NatsRuntime runtime) {
        this.server = server;
        this.runtime = runtime;
        this.parser = new NatsParser();
    }

    @Override
    public Optional<String> accept(SocketChannel clientSocketChannel, SelectionKey key) throws IOException {
        var info = getNewInfo(clientSocketChannel);
        this.runtime.connect(info.clientId);
        return info.print();
    }

    private Info getNewInfo(SocketChannel clientSocketChannel) {
        return new Info(clientSocketChannel.hashCode(), this.server.port);
    }

    @Override
    public Optional<String> request(SocketChannel clientSocketChannel, String line) throws IOException {
        if (line.equalsIgnoreCase(INFO_CMD)) {
            return this.getNewInfo(clientSocketChannel).print();
        }
        if (line.equalsIgnoreCase(QUIT_CMD) || line.equalsIgnoreCase(CTRLC_CMD)) {
            this.deregisterBuffer(clientSocketChannel);
            this.runtime.disconnect(clientSocketChannel.hashCode());
            clientSocketChannel.close();
            return Optional.empty();
        }
        if (line.equalsIgnoreCase(SHUTDOWN_CMD)) {
            this.deregisterBuffer(clientSocketChannel);
            this.runtime.disconnect(clientSocketChannel.hashCode());
            clientSocketChannel.close();
            this.server.stop();
            return Optional.empty();
        }

        var context = this.runtime.getContext(clientSocketChannel.hashCode());
        var command = this.parser.parse(new Request(this, clientSocketChannel), line);
        if (command.isPresent()) {
            var result = command.get().executeCommand(context);
            if (context.verbose() && result.isPresent()) {
                return Optional.of(result.get() + "[" + clientSocketChannel.hashCode() + "]" + ICmd.CRLF);
            } else {
                return result;
            }
        } else {
            return Optional.of("-E 'Command not found'" + ICmd.CRLF);
        }
    }

    public static class Request {

        private NatsHandler handler;
        private SocketChannel clientSocketChannel;

        public Request(NatsHandler handler, SocketChannel clientSocketChannel) {
            this.handler = handler;
            this.clientSocketChannel = clientSocketChannel;

        }

        public String readNextLine() throws IOException {
            return this.handler.readLineFromSocketChannel(clientSocketChannel);
        }

        public int clientId() {
            return this.clientSocketChannel.hashCode();
        }
    }

}
