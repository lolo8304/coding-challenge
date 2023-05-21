package redis.resp.cache;

import java.time.Duration;

import redis.resp.cache.RedisCache.RedisCacheContext;
import redis.resp.types.RespType;

public class ExpireIn extends ExpirationPolicy {

    public final Duration duration;

    public static ExpireIn seconds(Integer sec) {
        return new ExpireIn(Duration.ofSeconds(sec));
    }

    public static ExpireIn milliseconds(Long ms) {
        return new ExpireIn(Duration.ofMillis(ms));
    }

    public ExpireIn(Duration duration) {
        this.duration = duration;
    }

    @Override
    public boolean tryApplyToCacheContext(RespType value, RedisCacheContext context) {
        context.setTTL(this.duration);
        return (context.isAlive());
    }

}
