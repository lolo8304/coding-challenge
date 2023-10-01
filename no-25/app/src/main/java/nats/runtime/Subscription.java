package nats.runtime;

public class Subscription implements Comparable<Subscription> {
    public final int clientId;
    public final Subject subject;
    public final Subject queueGroup;
    public final String sid;
    private Topic topic;

    public Subscription(int clientId, Subject subject, Subject queueGroup, String sid) {
        this.clientId = clientId;
        this.subject = subject;
        this.queueGroup = queueGroup;
        this.sid = sid;
    }

    public void topic(Topic topic) {
        this.topic = topic;
    }

    public Topic topic() {
        return this.topic;
    }

    public Subscriptions parent() {
        return this.topic.parent();
    }

    public int publish(Subject anotherSubject, Subject replyTo, String payload) {
        this.parent().rawPublish(clientId, anotherSubject, this.sid, replyTo, payload);
        return 1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + clientId;
        result = prime * result + ((subject == null) ? 0 : subject.hashCode());
        result = prime * result + ((queueGroup == null) ? 0 : queueGroup.hashCode());
        result = prime * result + ((sid == null) ? 0 : sid.hashCode());
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
        Subscription other = (Subscription) obj;
        if (clientId != other.clientId)
            return false;
        if (subject == null) {
            if (other.subject != null)
                return false;
        } else if (!subject.equals(other.subject))
            return false;
        if (queueGroup == null) {
            if (other.queueGroup != null)
                return false;
        } else if (!queueGroup.equals(other.queueGroup))
            return false;
        if (sid == null) {
            if (other.sid != null)
                return false;
        } else if (!sid.equals(other.sid))
            return false;
        return true;
    }

    @Override
    public int compareTo(Subscription o) {
        return this.compareString().compareTo(o.compareString());
    }

    String compareString() {
        return String.format("%d-%s-%s-%s", this.clientId, this.sid, this.subject, this.queueGroup);
    }
}
