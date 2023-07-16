package memcached.server.cache;

public class NoExpiration extends ExpirationPolicy {

    @Override
    public boolean tryApplyToCacheContext(CacheContext context) {
        return true;
    }

}
