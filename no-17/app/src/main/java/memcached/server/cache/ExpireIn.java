package memcached.server.cache;

import java.time.Duration;

public class ExpireIn extends ExpirationPolicy {

    public final Duration duration;

    public static ExpireIn seconds(Integer sec) {
        return new ExpireIn(Duration.ofSeconds(sec));
    }

    public ExpireIn(Duration duration) {
        this.duration = duration;
    }

    @Override
    public boolean tryApplyToCacheContext(CacheContext context) {
        return (context.isAlive());
    }

}
