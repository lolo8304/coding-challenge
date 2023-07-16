package memcached.server.cache;

import memcached.commands.SetCommand;

public abstract class ExpirationPolicy {

    public static final NoExpiration NONE = new NoExpiration();

    public abstract boolean tryApplyToCacheContext(SetCommand command);

    public ExpirationPolicy add(ExpirationPolicy nextExpirationPolicy) {
        var newPolicies = new ExpirationPolicies(this);
        newPolicies.add(nextExpirationPolicy);
        return newPolicies;
    }

}
