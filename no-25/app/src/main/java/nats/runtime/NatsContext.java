package nats.runtime;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import nats.protocol.commands.Err;
import nats.protocol.commands.Msg;
import nats.protocol.commands.Pub;
import nats.protocol.commands.Sub;

public class NatsContext {
    private static final Logger _logger = Logger.getLogger(NatsContext.class.getName());
    private boolean verbose;
    private NatsRuntime runtime;
    private int clientId;
    private SocketChannel clientSocketChannel;

    public NatsContext(NatsRuntime runtime, int clientId) {
        this(runtime, clientId, null);
    }

    public NatsContext(NatsRuntime runtime, int clientId, SocketChannel clientSocketChannel) {
        this.clientSocketChannel = clientSocketChannel;
        this.runtime = runtime;
        this.clientId = clientId;
        this.verbose = false;
    }

    public void verbose(boolean value) {
        this.verbose = value;
    }

    public boolean verbose() {
        return this.verbose;
    }

    public void connect() {
        this.runtime.connect(this.clientId, this.clientSocketChannel);
    }

    public void disconnect() {
        this.runtime.disconnect(this.clientId);
    }

    public NatsRuntime runtime() {
        return this.runtime;
    }

    public void publish(Pub pub) {
        this.runtime.publish(pub.subject, pub.replyTo, pub.payload);
    }

    public Subscription subscribe(Sub sub) {
        return this.runtime.subscriptions().subscribe(this.clientId, new Subject(sub.subject),
                sub.queueGroup == null ? (Subject) null : new Subject(sub.queueGroup), sub.sid);
    }

    public void rawPublish(Subject subject, String sid, Subject replyTo, String payload) {
        var msg = new Msg(subject.subject, sid, replyTo == null ? null : replyTo.subject, payload.length(), payload);
        try {
            var printedMsg = msg.print();
            if (printedMsg.isPresent()) {
                if (_logger.isLoggable(Level.INFO))
                    _logger.info(
                            String.format("Context '%d' send msg subject '%s' sid '%s", this.clientId, subject.subject,
                                    sid));
                this.runtime.handler().write(this.clientSocketChannel, printedMsg.get());
            } else {
                this.runtime.handler().write(this.clientSocketChannel, new Err("msg-cannot-be-printed").print().get());
            }
        } catch (IOException e) {
            _logger.severe(String.format("Error writing raw message sid '%s' subject '%s' payload '%s'", msg.sid,
                    msg.subject, msg.payload));
        }
    }

}
