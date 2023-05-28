package redis.resp.cache;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.logging.Logger;

import redis.resp.IRespBuilder;
import redis.resp.RespException;
import redis.resp.RespScanner;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespArray;
import redis.resp.types.RespInteger;
import redis.resp.types.RespNull;
import redis.resp.types.RespSortedMap;
import redis.resp.types.RespType;

public class RedisCache implements IRespBuilder {
    static final Logger _logger = Logger.getLogger(RedisCache.class.getName());

    private final static Object SYNC = new Object();
    private final static String DUMP_NAME = "dump.rdb";

    private CacheMap keyCache;
    private LRUCache lruCache;

    private int memoryInBytes;
    private int maxMemoryBytes;

    private boolean blocking;

    public RedisCache() {
        this(300 * 1024);
    }

    public RedisCache(RespArray respArray) throws RespException {
        this.loadFrom(respArray);
    }

    public RedisCache(int maxMemoryBytes) {
        this.keyCache = new CacheMap(this);
        this.lruCache = new LRUCache(this, this.keyCache);
        this.memoryInBytes = 0;
        this.maxMemoryBytes = maxMemoryBytes;
        this.blocking = false;
    }

    public RespType set(String key, RespType value) throws RespCommandException {
        return set(key, value, ExpirationPolicy.NONE);
    }

    public RespType set(String key, RespType value, ExpirationPolicy expirationPolicy) throws RespCommandException {
        synchronized (SYNC) {
            var context = getValidContext(key);
            RespType returnValue;
            if (context.isEmpty()) {
                returnValue = value.valueForSetOperation(Optional.empty(), expirationPolicy);
                var newContext = new RedisCacheContext(this, key, value);
                if (newContext.trySetValue(value, expirationPolicy)) {
                    newContext.lastOperation = Operation.SET;
                    this.keyCache.put(key, newContext);
                    this.lruCache.put(key, newContext);
                } else {
                    return RespNull.NULL;
                }
            } else {
                returnValue = value.valueForSetOperation(Optional.of(context.get().getInternalValue()),
                        expirationPolicy);
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

    public boolean del(String key) {
        synchronized (SYNC) {
            var context = getValidContext(key);
            if (context.isPresent()) {
                this.removeContext(context.get());
            }
            return context.isPresent();
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

    public void registerMemoryInBytes(int oldMemoryInBytes, int newMemoryInBytes) {
        this.memoryInBytes += (newMemoryInBytes - oldMemoryInBytes);
        _logger.info("Memory used: " + fromMemoryInBytesToString(this.memoryInBytes) + " [ max="
                + fromMemoryInBytesToString(this.maxMemoryBytes) + ", high="
                + hasMaxMemoryReached()
                + "]");
    }

    public int getMemoryInBytes() {
        return this.memoryInBytes;
    }

    public static String fromMemoryInBytesToString(int memory) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        decimalFormat.setMaximumFractionDigits(1);
        if (memory > 1024) {
            int kb = memory / 1024;
            if (kb > 1024) {
                var mb = kb / 1024.0;
                return decimalFormat.format(mb) + " mb";
            } else {
                return decimalFormat.format(kb) + " kb";
            }
        } else {
            return memory + " byte";
        }
    }

    public boolean hasMaxMemoryReached() {
        return this.memoryInBytes > this.maxMemoryBytes;
    }

    public boolean hasBlockingOperation() {
        return this.blocking;
    }

    public void startBlockingOperation() {
        this.blocking = true;
    }

    public void stopBlockingOperation() {
        this.blocking = false;
    }

    public void load() {
        this.load(DUMP_NAME);
    }

    public void load(String fileName) {
        _logger.info("LOAD: start from " + fileName + " ...");
        File file = new File(fileName);
        if (file.exists()) {
            try {
                String content = Files.readString(Paths.get(fileName));
                var scanner = new RespScanner(RespScanner.convertNewLines(content));
                var next = scanner.next();
                if (next.isPresent()) {
                    var data = (RespArray) next.get();
                    loadFrom(data);
                }
                _logger.info("LOAD: ... done");
            } catch (IOException e) {
                _logger.severe("LOAD: Error reading from '" + fileName + "(" + e.getMessage() + ")");
            } catch (RespException e) {
                _logger.severe("LOAD: Error converting content (" + e.getMessage() + ")");
            }
        } else {
            _logger.info("LOAD: not found ... done");
        }
    }

    public void save() {
        this.save(DUMP_NAME);
    }

    public void save(String fileName) {
        _logger.info("SAVE: start to " + fileName + " ...");
        var buffer = new StringBuilder();
        this.toRespString(buffer);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            var content = RespScanner.convertNewLinesBack(buffer.toString());
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            _logger.severe("Error while writing to '" + fileName);
        }
        _logger.info("SAVE: ... done");
    }

    @Override
    public void toRespString(StringBuilder buffer) {
        this.toRespType().toRespString(buffer);
    }

    @Override
    public RespType toRespType() {
        return new RespSortedMap()
                .put("keyCache", this.keyCache.toRespType())
                .put("lruCache", this.lruCache.toRespType())
                .put("memoryInBytes", new RespInteger(memoryInBytes))
                .put("maxMemoryBytes", new RespInteger(maxMemoryBytes));
    }

    public void loadFrom(RespArray data) throws RespException {
        var map = data.arrayToMap();
        this.keyCache = new CacheMap(this, (RespArray) map.get("keyCache").get());
        this.lruCache = new LRUCache(this, this.keyCache, (RespArray) map.get("keyCache").get());
        this.memoryInBytes = map.get("memoryInBytes").get().getInteger();
    }

    public static class RedisCacheContext implements IRespBuilder {
        private final RedisCache cache;
        private String key;
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

        public RedisCacheContext(RedisCache cache, RespArray data) throws RespException {
            this.cache = cache;
            loadFrom(data);
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

        @Override
        public void toRespString(StringBuilder buffer) {
            this.toRespType().toRespString(buffer);
        }

        @Override
        public RespType toRespType() {
            return new RespSortedMap()
                    .put("key", this.key)
                    .put("value", this.value)
                    .put("operation", lastOperation.name())
                    .put("instant", this.lru.getEpochSecond())
                    .put("ttl",
                            this.ttl.isPresent()
                                    ? new RespSortedMap().put("seconds", this.ttl.get().getSeconds()).put("nanos",
                                            this.ttl.get().getNano())
                                    : RespNull.NULL)
                    .put("expirationTime",
                            this.expirationTime.isPresent()
                                    ? new RespInteger(this.expirationTime.get().getEpochSecond())
                                    : RespNull.NULL)
                    .put("memoryInBytes", this.memoryInBytes);
        }

        @Override
        public void loadFrom(RespArray data) throws RespException {
            var map = data.arrayToMap();
            this.key = map.get("key").get().getString();
            this.value = map.get("value").get();
            this.lastOperation = Operation.valueOf(map.get("operation").get().getString());

            var tmpTtl = map.get("ttl");
            if (tmpTtl.isPresent() && !tmpTtl.get().equals(RespNull.NULL)) {
                var ttlMap = ((RespArray) tmpTtl.get()).arrayToMap();
                var s = ttlMap.get("seconds").get().getLong();
                var nanos = ttlMap.get("nanos").get().getInteger();
                ttl = Optional.of(Duration.ofSeconds(s, nanos));
            } else {
                ttl = Optional.empty();
            }
            var tmpExpirationTime = map.get("expirationTime");
            if (tmpExpirationTime.isPresent() && !tmpExpirationTime.get().equals(RespNull.NULL)) {
                this.expirationTime = Optional.of(Instant.ofEpochMilli(tmpExpirationTime.get().getLong()));
            } else {
                this.expirationTime = Optional.empty();
            }

            this.memoryInBytes = map.get("memoryInBytes").get().getInteger();
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
