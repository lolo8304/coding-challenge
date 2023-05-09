package redis.resp;

import java.util.Optional;
import java.util.TreeMap;

import redis.resp.types.RespType;

public class RedisCache {

    private final static Object SYNC = new Object();

    private final TreeMap<String, RedisCacheContext> cache;

    public RedisCache() {
        this.cache = new TreeMap<>();
    }

    public RespType set(String key, RespType value) {
        synchronized (SYNC) {
            var context = getCache(key);
            RespType returnValue;
            if (context.isEmpty()) {
                returnValue = value.valueForSetOperation(Optional.empty());
                var newContext = new RedisCacheContext(this, key, value);
                newContext.lastOperation = Operation.SET;
                this.cache.put(key, newContext);
            } else {
                returnValue = value.valueForSetOperation(Optional.of(context.get().value));
                context.get().value = value;
                context.get().lastOperation = Operation.OVERRIDE;
            }
            return returnValue;
        }
    }

    public <T extends RespType> Optional<T> get(String key) {
        synchronized (SYNC) {
            var context = getCache(key);
            if (context.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of((T) context.get().value);
            }
        }
    }

    private Optional<RedisCacheContext> getCache(String key) {
        var context = this.cache.get(key);
        if (context == null) {
            return Optional.empty();
        } else {
            return Optional.of(context);
        }
    }

    public static class RedisCacheContext {
        private RedisCache cache;
        private String key;
        private RespType value;
        private Operation lastOperation;

        public RedisCacheContext(RedisCache cache, String key, RespType value) {
            this.cache = cache;
            this.key = key;
            this.value = value;
            this.lastOperation = Operation.UNDEFINED;
        }
    }

    public enum Operation {
        UNDEFINED,
        SET,
        OVERRIDE,
        CLEARED,
        EXPIRED
    }

}
