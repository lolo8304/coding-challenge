package nats.runtime;

import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Map;

import nats.protocol.NatsHandler;

public class NatsRuntime {
    private final Map<Integer, NatsContext> contexts;
    private final Subscriptions subscriptions;
    private final NatsHandler handler;

    public NatsRuntime(NatsHandler handler) {
        this.handler = handler;
        this.contexts = new Hashtable<>();
        this.subscriptions = new Subscriptions(this);
    }

    public Subscriptions subscriptions() {
        return this.subscriptions;
    }

    public NatsHandler handler() {
        return this.handler;
    }

    public NatsContext getContext(int clientId) {
        return this.contexts.get(clientId);
    }

    public NatsContext connect(int clientId, SocketChannel clientSocketChannel) {
        var context = this.contexts.get(clientId);
        return context == null ? this.contexts.put(clientId, new NatsContext(this, clientId, clientSocketChannel))
                : context;
    }

    public void disconnect(int clientId) {
        this.subscriptions.disconnect(clientId);
        this.contexts.remove(clientId);
    }

    public int publish(String subject, String replyTo, String payload) {
        return this.subscriptions.publish(new Subject(subject), replyTo == null ? null : new Subject(replyTo), payload);
    }

    public void rawPublish(int clientId, Subject subject, String sid, Subject replyTo, String payload) {
        var ctx = this.getContext(clientId);
        if (ctx != null) {
            ctx.rawPublish(subject, sid, replyTo, payload);
        }
    }
}
