package redis.resp.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache {
    private final LinkedHashMap<String, RedisCache.RedisCacheContext> cache;
    private RedisCache redisCache;

    public LRUCache(RedisCache redisCache) {
        this.redisCache = redisCache;
        this.cache = new LinkedHashMap<String, RedisCache.RedisCacheContext>(0, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, RedisCache.RedisCacheContext> eldest) {
                var toRemove = redisCache.hasMaxMemoryReached();
                if (toRemove) {
                    eldest.getValue().evict();
                }
                return toRemove;
            }
        };
    }

    public synchronized RedisCache.RedisCacheContext get(String key) {
        RedisCache.RedisCacheContext value = cache.get(key);
        if (value != null) {
            // Move the accessed entry to the end of the access order
            cache.remove(key);
            cache.put(key, value);
        }
        return value;
    }

    public synchronized void put(String key, RedisCache.RedisCacheContext value) {
        RedisCache.RedisCacheContext oldValue = cache.get(key);
        if (oldValue != null) {
            // Move the accessed entry to the end of the access order
            cache.remove(key);
            cache.put(key, value);
        } else {
            cache.put(key, value);
        }
    }

    public synchronized void remove(String key) {
        cache.remove(key);
    }

    public synchronized void clear() {
        cache.clear();
    }

    public synchronized int size() {
        return cache.size();
    }

    public synchronized boolean containsKey(String key) {
        return cache.containsKey(key);
    }
}
