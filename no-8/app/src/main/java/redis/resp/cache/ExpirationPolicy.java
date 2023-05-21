package redis.resp.cache;

import java.util.Optional;

import redis.resp.cache.RedisCache.RedisCacheContext;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespType;

public abstract class ExpirationPolicy {

    public static final NoExpiration NONE = new NoExpiration();
    public static final SetIfExists SET_IF_EXISTS = new SetIfExists();
    public static final SetIfNotExists SET_IF_NOT_EXISTS = new SetIfNotExists();
    public static final KeepTtl KEEP_TTL = new KeepTtl();
    public static final GetPolicy GET_POLICY = new GetPolicy();

    public abstract boolean tryApplyToCacheContext(RespType value, RedisCacheContext context);

    public ExpirationPolicy add(ExpirationPolicy nextExpirationPolicy) {
        var newPolicies = new ExpirationPolicies(this);
        newPolicies.add(nextExpirationPolicy);
        return newPolicies;
    }

    // default implementation is not change any return value from standard behavior
    public Optional<RespType> changedValueForSetOperation(RespType newValue, Optional<RespType> oldValue)
            throws RespCommandException {
        return Optional.empty();
    }
}
