package redis.resp.cache;

import java.time.Instant;

import redis.resp.cache.RedisCache.RedisCacheContext;
import redis.resp.types.RespType;

public class ExpireAt extends ExpirationPolicy {

    public final Instant timestamp;

    public static ExpireAt seconds(Integer sec) {
        return new ExpireAt(Instant.ofEpochSecond(sec));
    }

    public static ExpireAt milliseconds(Long ms) {
        return new ExpireAt(Instant.ofEpochMilli(ms));
    }

    public ExpireAt(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean tryApplyToCacheContext(RespType value, RedisCacheContext context) {
        context.setExpirationTime(this.timestamp);
        return context.isAlive();
    }

}
