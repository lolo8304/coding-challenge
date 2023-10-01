package nats.runtime;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Topic {
    private static final Logger _logger = Logger.getLogger(Topic.class.getName());
    private static Random RANDOM = new SecureRandom();

    private Subject subject;
    private Set<Subscription> subscriptions;
    private Map<Subject, List<Subscription>> workGroupSubscriptions;
    private Subscriptions parent;

    public Topic(Subscriptions parent, Subject subject) {
        this.parent = parent;
        this.subject = subject;
        this.subscriptions = new TreeSet<>();
        this.workGroupSubscriptions = new Hashtable<>();
    }

    public Subject subject() {
        return subject;
    }

    public Subscriptions parent() {
        return this.parent;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((subject == null) ? 0 : subject.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Topic other = (Topic) obj;
        if (subject == null) {
            if (other.subject != null)
                return false;
        } else if (!subject.equals(other.subject))
            return false;
        return true;
    }

    public void disconnect(int clientId) {
        new ArrayList<Subscription>(this.subscriptions).forEach((s) -> {
            if (s.clientId == clientId) {
                this.subscriptions.remove(s);
                if (_logger.isLoggable(Level.INFO))
                    _logger.info(String.format("SUB: remove subscription subject '%s' by client id '%d'",
                            s.subject.subject, s.clientId));
            }
        });
        new ArrayList<Subject>(this.workGroupSubscriptions.keySet()).forEach((subj) -> {
            new ArrayList<Subscription>(this.workGroupSubscriptions.get(subj)).forEach((s) -> {
                if (s.clientId == clientId) {
                    this.workGroupSubscriptions.get(subj).remove(s);
                    if (_logger.isLoggable(Level.INFO))
                        _logger.info(String.format("SUB: remove subscription subject '%s' queue '%s' by client id '%d'",
                                s.subject.subject, s.queueGroup.subject, s.clientId));
                }
            });
            if (this.workGroupSubscriptions.get(subj).isEmpty()) {
                this.workGroupSubscriptions.remove(subj);
                if (_logger.isLoggable(Level.INFO))
                    _logger.info(
                            String.format(
                                    String.format("SUB: remove last subscription from queue '%s'", subj.subject)));
            }
        });
    }

    public Subscription addSubscription(Subscription subscription) {
        if (subscription.queueGroup == null) {
            this.subscriptions.add(subscription);
        } else {
            var foundQueueGroups = this.workGroupSubscriptions.get(subscription.queueGroup);
            if (foundQueueGroups == null) {
                foundQueueGroups = new ArrayList<>();
                this.workGroupSubscriptions.put(subscription.queueGroup, foundQueueGroups);
            }
            foundQueueGroups.add(subscription);
        }
        subscription.topic(this);
        return subscription;
    }

    public boolean subscribedTo(Subject anotherSubject) {
        return this.subject.subscribedTo(anotherSubject);
    }

    public int publish(Subject anotherSubject, Subject replyTo, String payload) {
        var count = 0;
        var keys = this.workGroupSubscriptions.keySet().toArray(Subject[]::new);
        for (int i = 0; i < keys.length; i++) {
            var key = keys[i];
            var value = this.workGroupSubscriptions.get(key);

            var tmpList = new ArrayList<>(value);
            var delivered = 0;
            while (delivered == 0 && tmpList.size() > 0) {
                var index = RANDOM.nextInt(tmpList.size());
                var subToDeliver = tmpList.get(index);
                delivered += subToDeliver.publish(anotherSubject, replyTo, payload);
                if (delivered > 0) {
                    tmpList.remove(index);
                }
            }
            count += delivered;
            if (delivered == 0 && tmpList.size() > 0) {
                _logger.warning(String.format("MSG: not published to queue group '%s' from %d subscribers",
                        key, value.size()));
            }
        }
        for (Subscription subscription : subscriptions) {
            count += subscription.publish(anotherSubject, replyTo, payload);
        }
        return count;
    }

}
