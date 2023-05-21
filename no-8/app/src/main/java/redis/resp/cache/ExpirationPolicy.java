package redis.resp.cache;

import redis.resp.cache.RedisCache.RedisCacheContext;
import redis.resp.types.RespType;

public abstract class ExpirationPolicy {

    public final static NoExpiration NONE = new NoExpiration();
    public final static SetIfExists SET_IF_EXISTS = new SetIfExists();
    public final static SetIfNotExists SET_IF_NOT_EXISTS = new SetIfNotExists();
    public final static KeepTtl KEEP_TTL = new KeepTtl();

    public abstract boolean tryApplyToCacheContext(RespType value, RedisCacheContext context);

}
