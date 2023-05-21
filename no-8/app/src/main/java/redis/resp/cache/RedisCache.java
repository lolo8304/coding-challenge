package redis.resp.cache;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.TreeMap;
import java.util.logging.Logger;

import redis.resp.types.RespNull;
import redis.resp.types.RespType;

public class RedisCache {
    static final Logger _logger = Logger.getLogger(RedisCache.class.getName());

    private final static Object SYNC = new Object();

    private final TreeMap<String, RedisCacheContext> keyCache;
    private final LRUCache lruCache;

    private int memoryInBytes;
    private int maxMemoryBytes;

    public RedisCache() {
        this(200 * 1024);
    }

    public RedisCache(int maxMemoryBytes) {
        this.keyCache = new TreeMap<>();
        this.lruCache = new LRUCache(this);
        this.memoryInBytes = 0;
        this.maxMemoryBytes = maxMemoryBytes;
    }

    public RespType set(String key, RespType value) {
        return set(key, value, ExpirationPolicy.NONE);
    }

    public RespType set(String key, RespType value, ExpirationPolicy expirationPolicy) {
        synchronized (SYNC) {
            var context = getValidContext(key);
            RespType returnValue;
            if (context.isEmpty()) {
                returnValue = value.valueForSetOperation(Optional.empty());
                var newContext = new RedisCacheContext(this, key, value);
                if (newContext.trySetValue(value, expirationPolicy)) {
                    newContext.lastOperation = Operation.SET;
                    this.keyCache.put(key, newContext);
                    this.lruCache.put(key, newContext);
                } else {
                    return RespNull.NULL;
                }
            } else {
                returnValue = value.valueForSetOperation(Optional.of(context.get().getInternalValue()));
                if (context.get().trySetValue(value, expirationPolicy)) {
                    context.get().lastOperation = Operation.OVERRIDE;
                } else {
                    return RespNull.NULL;
                }
            }
            return returnValue;
        }
    }

    public <T extends RespType> Optional<T> get(String key) {
        synchronized (SYNC) {
            var context = getValidContext(key);
            if (context.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of((T) context.get().getValue());
            }
        }
    }

    private Optional<RedisCacheContext> getValidContext(String key) {
        var context = this.getContext(key);
        if (context.isPresent() && context.get().tryAlive()) {
            return context;
        } else {
            return Optional.empty();
        }
    }

    public boolean hasTTL(String key) {
        var context = this.getContext(key);
        return context.isPresent() && context.get().hasTTL();
    }

    public boolean neverExpire(String key) {
        var context = this.getContext(key);
        return context.isPresent() && context.get().neverExpire();
    }

    public boolean isExpired(String key) {
        var context = this.getValidContext(key);
        return context.isEmpty() || context.get().isExpired();
    }

    public boolean isAlive(String key) {
        var context = this.getValidContext(key);
        return context.isPresent() && context.get().isAlive();
    }

    public boolean willExpireIn(String key, Duration duration) {
        var context = this.getContext(key);
        return context.isPresent() && context.get().willExpireIn(duration);
    }

    public boolean willExpireIn(String key, ExpireIn expireIn) {
        var context = this.getContext(key);
        return context.isPresent() && context.get().willExpireIn(expireIn.duration);
    }

    public boolean willExpireAt(String key, Instant expireAt) {
        var context = this.getContext(key);
        return context.isPresent() && context.get().willExpireAt(expireAt);
    }

    public boolean willExpireAt(String key, ExpireAt expireAt) {
        var context = this.getContext(key);
        return context.isPresent() && context.get().willExpireAt(expireAt.timestamp);
    }

    private Optional<RedisCacheContext> getContext(String key) {
        var context = this.keyCache.get(key);
        if (context == null) {
            return Optional.empty();
        } else {
            this.lruCache.get(key); // just to keep lru cache active
            return Optional.of(context);
        }
    }

    private Optional<RedisCacheContext> removeContext(RedisCacheContext context) {
        var removed = this.keyCache.remove(context.key);
        if (removed == null) {
            return Optional.empty();
        } else {
            this.memoryInBytes -= context.memoryInBytes;
            this.lruCache.remove(context.key);
            return Optional.of(removed);
        }
    }

    public static class RedisCacheContext {
        private final RedisCache cache;
        private final String key;
        private RespType value;
        private Operation lastOperation;
        private Instant lru;

        private Optional<Duration> ttl;
        private Optional<Instant> expirationTime;
        private int memoryInBytes = 0;

        public RedisCacheContext(RedisCache cache, String key, RespType value) {
            this.cache = cache;
            this.key = key;
            this.value = value;
            this.lastOperation = Operation.UNDEFINED;
            this.ttl = Optional.empty();
            this.expirationTime = Optional.empty();
            this.touch();
            this.registerMemoryInBytes();
        }

        public RespType getValue() {
            this.touch();
            return this.value;
        }

        public RespType getInternalValue() {
            return this.value;
        }

        public void setTTLinMs(int ms) {
            this.setTTL(Duration.ofMillis(ms));
        }

        public void setTTLinSec(int sec) {
            this.setTTL(Duration.ofSeconds(sec));
        }

        public void setTTL(Duration duration) {
            this.ttl = Optional.of(duration);
            this.expirationTime = Optional.of(Instant.now().plus(duration));
        }

        public void setExpirationTime(Instant at) {
            this.expirationTime = Optional.of(at);
            this.ttl = Optional.of(Duration.between(Instant.now(), this.expirationTime.get()));
        }

        public void setExpirationTimeInMs(long ms) {
            this.setExpirationTime(Instant.ofEpochMilli(ms));
        }

        public void setExpirationTimeInSec(long sec) {
            this.setExpirationTime(Instant.ofEpochSecond(sec));
        }

        public void keepTTL() {
            if (this.ttl.isPresent()) {
                this.setTTL(this.ttl.get());
            }
        }

        public boolean isAlive() {
            return this.ttl.isEmpty() || this.expirationTime.get().isAfter(Instant.now());
        }

        public boolean isExpired() {
            return !isAlive();
        }

        public boolean willExpireIn(Duration duration) {
            return this.ttl.isPresent() && this.expirationTime.get().isBefore(Instant.now().plus(duration));
        }

        public boolean willExpireAt(Instant expireAt) {
            return this.ttl.isPresent() && this.expirationTime.get().isBefore(expireAt);
        }

        public boolean tryExpiration() {
            if (this.isAlive()) {
                return false;
            } else {
                _logger.info("Key expired: " + this.key + " and removed");
                this.cache.removeContext(this);
                return true;
            }
        }

        public void evict() {
            _logger.info("Key evicted: " + this.key + " and removed");
            this.cache.removeContext(this);
        }

        public boolean tryAlive() {
            return !this.tryExpiration();
        }

        public boolean hasTTL() {
            return this.ttl.isPresent();
        }

        public boolean neverExpire() {
            return this.ttl.isEmpty();
        }

        public boolean trySetValue(RespType value, ExpirationPolicy expirationPolicy) {
            var result = expirationPolicy.tryApplyToCacheContext(value, this);
            if (result) {
                this.value = value;
                this.touch();
                this.registerMemoryInBytes();
            }
            return result;
        }

        private void touch() {
            this.lru = Instant.now();
        }

        public void registerMemoryInBytes() {
            var oldMemory = this.memoryInBytes;
            this.memoryInBytes = this.value.toRespString().length();
            this.cache.registerMemoryInBytes(oldMemory, this.memoryInBytes);
        }

        public boolean hasValueSet() {
            return this.lastOperation == Operation.SET || this.lastOperation == Operation.OVERRIDE;
        }

        public boolean hasNoValueSet() {
            return this.lastOperation == Operation.UNDEFINED;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            RedisCacheContext other = (RedisCacheContext) obj;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            return true;
        }
    }

    public enum Operation {
        UNDEFINED,
        SET,
        OVERRIDE,
        CLEARED,
        EXPIRED
    }

    public void registerMemoryInBytes(int oldMemoryInBytes, int newMemoryInBytes) {
        this.memoryInBytes += (newMemoryInBytes - oldMemoryInBytes);
        _logger.info("Memory used: " + this.getMemoryInBytesToString() + " [ higer than max = " + hasMaxMemoryReached()
                + "]");
    }

    public int getMemoryInBytes() {
        return this.memoryInBytes;
    }

    public String getMemoryInBytesToString() {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        decimalFormat.setMaximumFractionDigits(1);
        if (this.memoryInBytes > 1024) {
            int kb = this.memoryInBytes / 1024;
            if (kb > 1024) {
                var mb = kb / 1024.0;
                return decimalFormat.format(mb) + " mb";
            } else {
                return decimalFormat.format(kb) + " kb";
            }
        } else {
            return this.memoryInBytes + " byte";
        }
    }

    public boolean hasMaxMemoryReached() {
        return this.memoryInBytes > this.maxMemoryBytes;
    }

}
