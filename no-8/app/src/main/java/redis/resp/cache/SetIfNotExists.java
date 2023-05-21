package redis.resp.cache;

import redis.resp.cache.RedisCache.RedisCacheContext;
import redis.resp.types.RespType;

public class SetIfNotExists extends ExpirationPolicy {

    @Override
    public boolean tryApplyToCacheContext(RespType value, RedisCacheContext context) {
        return context.hasNoValueSet();
    }

}
