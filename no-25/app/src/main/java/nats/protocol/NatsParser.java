package nats.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import nats.protocol.commands.Connect;
import nats.protocol.commands.ICmd;
import nats.protocol.commands.Ping;
import nats.protocol.commands.Pong;
import nats.protocol.commands.Pub;
import nats.protocol.commands.Sub;

public class NatsParser {

    private Map<String, Function<NatsLineParser, ICmd>> commands;

    public NatsParser() {
        this.commands = new HashMap<String, Function<NatsLineParser, ICmd>>();
        this.initCmds();
    }

    private void initCmds() {
        this.commands.put("CONNECT", Connect::new);
        this.commands.put("PING", Ping::new);
        this.commands.put("PONG", Pong::new);
        this.commands.put("PUB", Pub::new);
        this.commands.put("SUB", Sub::new);
    }

    public Optional<ICmd> parse(NatsHandler.Request request, String line) {
        var lineParser = new NatsLineParser(line, request);
        var op = lineParser.nextToken();
        if (op.isPresent()) {
            var opUpper = op.get().toString().toUpperCase();
            var cmd = this.commands.get(opUpper);
            if (cmd != null) {
                return Optional.of((cmd.apply(lineParser)));
            }
            return Optional.empty();
        } else {
            throw new IllegalStateException("Expected NATS operation in line '" + line + "'");
        }
    }

}
