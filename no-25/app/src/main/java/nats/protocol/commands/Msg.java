package nats.protocol.commands;

import java.io.IOException;
import java.util.Optional;

import nats.protocol.NatsLineParser;
import nats.protocol.NatsLineParser.Type;
import nats.runtime.NatsContext;
import nats.runtime.Subject;

public class Msg implements ICmd {

    public String subject;
    public String sid;
    public String replyTo;
    public int nofBytes;
    public String payload;

    public Msg() {
    }

    public Msg(String subject, String sid, String replyTo, int nofBytes, String payload) {
        this.subject = subject;
        this.sid = sid;
        this.replyTo = replyTo;
        this.nofBytes = nofBytes;
        this.payload = payload;
    }

    public Msg(NatsLineParser line) {
        build(line);
    }

    private void build(NatsLineParser line) {
        var subjectToken = line.nextToken();
        if (subjectToken.isPresent()) {
            this.subject = subjectToken.get().toString();
        } else {
            throw new IllegalArgumentException("Cmd MSG: Subject name missing");
        }
        var sidToken = line.nextToken();
        if (sidToken.isPresent()) {
            this.sid = sidToken.get().toString();
        } else {
            throw new IllegalArgumentException("Cmd MSG: sid name missing");
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
                throw new IllegalArgumentException("Cmd MSG: #bytes is not present after reply-to");
            }
        } else {
            throw new IllegalArgumentException("Cmd MSG: at least #bytes must be present");
        }
        try {
            var payloadToken = line.readNextLine().nextToken();
            if (payloadToken.isPresent()) {
                this.payload = payloadToken.get().toString();
                if (this.nofBytes != this.payload.length()) {
                    throw new IllegalArgumentException(
                            String.format("Cmd MSG: payload (%d) has not same size according to #bytes (%s)",
                                    this.payload.length(), this.nofBytes));
                }
            } else {
                throw new IllegalArgumentException("Cmd MSG: no further payload available");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Cmd MSG: no further payload available", e);
        }

    }

    @Override
    public Optional<String> print() throws IOException {
        var builder = new StringBuilder();
        builder.append("MSG").append(' ').append(this.subject).append(' ').append(this.sid);
        if (this.replyTo != null) {
            builder.append(' ').append(this.replyTo);
        }
        builder.append(' ').append(this.nofBytes).append(CRLF).append(this.payload).append(CRLF);
        return Optional.of(builder.toString());
    }

    @Override
    public Optional<String> executeCommand(NatsContext context) throws IOException {
        context.rawPublish(new Subject(this.subject), this.sid, replyTo == null ? null : new Subject(replyTo), payload);
        return Optional.empty();
    }

}
