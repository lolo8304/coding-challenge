package nats.protocol.commands;

import java.io.IOException;
import java.util.Optional;

import nats.protocol.NatsLineParser;
import nats.protocol.NatsLineParser.Type;
import nats.runtime.NatsContext;

public class Pub implements ICmd {

    public String subject;
    public String replyTo;
    public int nofBytes;
    public String payload;

    public Pub() {
    }

    public Pub(NatsLineParser line) {
        build(line);
    }

    private void build(NatsLineParser line) {
        var subjectToken = line.nextToken();
        if (subjectToken.isPresent()) {
            this.subject = subjectToken.get().toString();
        } else {
            throw new IllegalArgumentException("Cmd PUB: Subject name missing");
        }
        var byteToken = line.nextToken();
        // check if token is String --> use optional reply-to, if not bytes
        if (byteToken.isPresent()) {
            if (byteToken.get().type() == Type.STRING) {
                this.replyTo = byteToken.get().toString();
                byteToken = line.nextToken();
            }
            if (byteToken.isPresent()) {
                this.nofBytes = byteToken.get().toInt();
            } else {
                throw new IllegalArgumentException("Cmd PUB: #bytes is not present after reply-to");
            }
        } else {
            throw new IllegalArgumentException("Cmd PUB: at least #bytes must be present");
        }
        try {
            var payloadToken = line.readNextLine().nextToken();
            if (payloadToken.isPresent()) {
                this.payload = payloadToken.get().toString();
                if (this.nofBytes != this.payload.length()) {
                    throw new IllegalArgumentException(
                            String.format("Cmd PUB: payload (%d) has not same size according to #bytes (%s)",
                                    this.payload.length(), this.nofBytes));
                }
            } else {
                throw new IllegalArgumentException("Cmd PUB: no further payload available");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Cmd PUB: no further payload available", e);
        }

    }

    @Override
    public Optional<String> print() throws IOException {
        var builder = new StringBuilder();
        builder.append("PUB").append(' ').append(this.subject);
        if (this.replyTo != null) {
            builder.append(' ').append(this.replyTo);
        }
        builder.append(' ').append(this.nofBytes).append(CRLF).append(this.payload).append(CRLF);
        return Optional.of(builder.toString());
    }

    @Override
    public Optional<String> executeCommand(NatsContext context) throws IOException {
        context.publish(this);
        if (context.verbose()) {
            return Optional.of("Ok" + CRLF);
        }
        return Optional.empty();
    }

}
