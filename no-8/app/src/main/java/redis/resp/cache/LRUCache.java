package redis.resp.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import redis.resp.IRespBuilder;
import redis.resp.RespException;
import redis.resp.types.RespArray;
import redis.resp.types.RespSortedMap;
import redis.resp.types.RespType;

public class LRUCache implements IRespBuilder {
    private final LinkedHashMap<String, RedisCache.RedisCacheContext> cache;
    private RedisCache redisCache;
    private CacheMap cacheMap;
    private boolean loading;

    public LRUCache(RedisCache redisCache, CacheMap cacheMap, RespArray data) throws RespException {
        this(redisCache, cacheMap);
        this.loading = true;
        this.loadFrom(data);
        this.loading = false;
    }

    public LRUCache(RedisCache redisCache, CacheMap cacheMap) {
        this.redisCache = redisCache;
        this.cacheMap = cacheMap;
        this.loading = false;
        this.cache = new LinkedHashMap<String, RedisCache.RedisCacheContext>(0, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, RedisCache.RedisCacheContext> eldest) {
                if (loading) {
                    return false;
                }
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

    @Override
    public void toRespString(StringBuilder buffer) {
        this.toRespType().toRespString(buffer);
    }

    @Override
    public RespType toRespType() {
        var map = new RespSortedMap();
        this.cache.forEach((key, value) -> {
            map.put(key, value.toRespType());
        });
        return map;
    }

    @Override
    public void loadFrom(RespArray data) throws RespException {
        data.arrayToMap().forEach((key, value) -> {
            this.put(key, this.cacheMap.get(key));
        });
    }
}
