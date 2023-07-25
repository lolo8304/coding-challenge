package memcached.server.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache {
    private final LinkedHashMap<String, CacheContext> cache;
    private MemCache memCache;

    public LRUCache(MemCache cache) {
        this.memCache = cache;
        this.cache = new LinkedHashMap<String, CacheContext>(0, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CacheContext> eldest) {
                var toRemove = memCache.hasMaxMemoryReached();
                if (toRemove) {
                    eldest.getValue().evict();
                }
                return toRemove;
            }
        };
    }

    public synchronized CacheContext get(String key) {
        CacheContext value = cache.get(key);
        if (value != null) {
            // Move the accessed entry to the end of the access order
            cache.remove(key);
            cache.put(key, value);
        }
        return value;
    }

    public synchronized void put(String key, CacheContext value) {
        CacheContext oldValue = cache.get(key);
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
