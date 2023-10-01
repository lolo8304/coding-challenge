package nats.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Subscriptions {
    private static final Logger _logger = Logger.getLogger(Subscriptions.class.getName());
    private Map<Subject, Topic> topics;
    private NatsRuntime natsRuntime;

    public Subscriptions(NatsRuntime natsRuntime) {
        this.natsRuntime = natsRuntime;
        this.topics = new HashMap<>();
    }

    private Topic getIfAbsentPutNew(Subject subject) {
        var topic = this.topics.get(subject);
        if (topic == null) {
            topic = new Topic(this, subject);
            this.topics.put(subject, topic);
        }
        return topic;
    }

    public void disconnect(int clientId) {
        this.topics.values().forEach((t) -> {
            t.disconnect(clientId);
        });
    }

    public Subscription subscribe(Subscription subscription) {
        this.getIfAbsentPutNew(subscription.subject).addSubscription(subscription);
        return subscription;
    }

    public Subscription subscribe(int clientId, Subject subject, Subject queueGroup, String sid) {
        var subscription = new Subscription(clientId, subject, queueGroup, sid);
        return this.subscribe(subscription);
    }

    public int publish(Subject subject, Subject replyTo, String payload) {
        var topics = this.topicsSubscribedTo(subject);
        var count = 0;
        for (Topic topic : topics) {
            count += topic.publish(subject, replyTo, payload);
        }
        if (count == 0) {
            if (_logger.isLoggable(Level.INFO))
                _logger.info(String.format("PUB: msg with subject '%s' discarded. No subscribers", subject.subject));
        }
        return count;
    }

    public List<Topic> topicsSubscribedTo(Subject subject) {
        return this.topics.values().stream().filter((x) -> x.subscribedTo(subject)).toList();
    }

    public void rawPublish(int clientId, Subject anotherSubject, String sid, Subject replyTo, String payload) {
        this.natsRuntime.rawPublish(clientId, anotherSubject, sid, replyTo, payload);
    }

}
