package memcached.server.cache;

public abstract class ExpirationPolicy {

    public static final NoExpiration NONE = new NoExpiration();

    public abstract boolean tryApplyToCacheContext(CacheContext context);

    public ExpirationPolicy add(ExpirationPolicy nextExpirationPolicy) {
        var newPolicies = new ExpirationPolicies(this);
        newPolicies.add(nextExpirationPolicy);
        return newPolicies;
    }

}
