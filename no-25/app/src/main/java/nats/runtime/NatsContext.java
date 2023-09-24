package nats.runtime;

import nats.NatsRuntime;
import nats.protocol.commands.Pub;
import nats.protocol.commands.Sub;

public class NatsContext {

    private boolean verbose;
    private NatsRuntime runtime;
    private int clientId;

    public NatsContext(NatsRuntime runtime, int clientId) {
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
        this.runtime.connect(this.clientId);
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

    public void subscribe(Sub sub) {
    }

}
