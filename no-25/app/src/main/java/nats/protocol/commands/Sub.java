package nats.protocol.commands;

import java.io.IOException;
import java.util.Optional;

import nats.protocol.NatsLineParser;
import nats.runtime.NatsContext;

public class Sub implements ICmd {

    public String subject;
    public String queueGroup;
    public String sid;

    public Sub() {
    }

    public Sub(NatsLineParser line) {
        build(line);
    }

    private void build(NatsLineParser line) {
        var subjectToken = line.nextToken();
        if (subjectToken.isPresent()) {
            this.subject = subjectToken.get().toString();
        } else {
            throw new IllegalArgumentException("Cmd SUB: Subject name missing");
        }
        // read 2 ahead to see if 1 optional queueGroup is used or not
        var queueGroupToken = line.nextToken();
        var sidToken = line.nextToken();
        if (queueGroupToken.isPresent()) {
            if (sidToken.isEmpty()) { // no queue group
                this.queueGroup = null;
                sidToken = queueGroupToken;
            } else {
                this.queueGroup = queueGroupToken.get().toString();
            }
            this.sid = sidToken.get().toString();
        } else {
            throw new IllegalArgumentException("Cmd SUB: at least sid must be present");
        }
    }

    @Override
    public Optional<String> print() throws IOException {
        return Optional.of("CONNECT {} " + CRLF);
    }

    @Override
    public Optional<String> executeCommand(NatsContext context) throws IOException {
        if (context.verbose()) {
            return Optional.of("Ok" + CRLF);
        }
        return Optional.empty();
    }

}
