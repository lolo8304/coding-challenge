package nats;

import java.util.Hashtable;
import java.util.Map;

import nats.runtime.NatsContext;

public class NatsRuntime {
    private Map<Integer, NatsContext> contexts;

    public NatsRuntime() {
        this.contexts = new Hashtable<>();
    }

    public NatsContext getContext(int clientId) {
        return this.contexts.get(clientId);
    }

    public NatsContext connect(int clientId) {
        var context = this.contexts.get(clientId);
        return context == null ? this.contexts.put(clientId, new NatsContext(this, clientId)) : context;
    }

    public void disconnect(int clientId) {
        this.contexts.remove(clientId);
    }

    public void publish(String subject, String replyTo, String payload) {
    }
}
