package memcached.server.cache;

import java.text.DecimalFormat;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import memcached.commands.Command;
import memcached.commands.SetCommand;
import memcached.commands.ValidationCode;

public class MemCache {
    private static final Logger _logger = Logger.getLogger(MemCache.class.getName());
    private Dictionary<String, CacheContext> cache;
    private LRUCache lruCache;
    private int memoryInBytes;
    private int maxMemoryBytes;

    public MemCache() {
        this(300 * 1024);
    }

    public MemCache(int maxMemoryBytes) {
        this.cache = new Hashtable<>();
        this.lruCache = new LRUCache(this);
        this.memoryInBytes = 0;
        this.maxMemoryBytes = maxMemoryBytes;
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

    private void registerMemoryInBytes(int oldValue, int newValue) {
        this.memoryInBytes += (newValue - oldValue);
        if (_logger.isLoggable(Level.INFO)) {
            _logger.info("Memory used: " + fromMemoryInBytesToString(this.memoryInBytes) + " [ max="
                    + fromMemoryInBytesToString(this.maxMemoryBytes) + ", high="
                    + hasMaxMemoryReached()
                    + "]");
        }
    }

    public Optional<ValidationCode> set(SetCommand cmd) {
        var validationResult = cmd.isValidToAddToCache(this);
        if (validationResult.equals(ValidationCode.OK)) {
            var existingValue = this.getValidContext(cmd.key);
            if (existingValue.isPresent()) {
                var oldValue = existingValue.get().bytes();
                var result = cmd.setToContext(existingValue.get());
                this.registerMemoryInBytes(oldValue, cmd.bytes());
                return result;
            } else {
                var newContext = new CacheContext(this, cmd);
                this.cache.put(cmd.key, newContext);
                this.lruCache.put(null, newContext);
                this.registerMemoryInBytes(0, cmd.bytes());
                return Optional.of(ValidationCode.STORED);
            }
        } else {
            return Optional.of(validationResult);
        }
    }

    private Optional<CacheContext> getValidContext(String key) {
        var value = this.cache.get(key);
        if (value != null) {
            this.lruCache.get(key); // just to keep lru cache active
            if (value.isAlive()) {
                return Optional.of(value);
            } else {
                value.evict();
            }
        }
        return Optional.empty();
    }

    public ValidationCode deleteKey(String key) {
        var context = this.getValidContext(key);
        if (context.isPresent()) {
            context.get().evict();
            return ValidationCode.DELETED;
        } else {
            return ValidationCode.NOT_FOUND;
        }
    }

    public Optional<SetCommand> get(String key) {
        var context = this.getValidContext(key);
        return context.isPresent() ? Optional.of(context.get().command()) : Optional.empty();
    }

    public Optional<SetCommand> get(String key, int cas) {
        var context = this.getValidContext(key);
        if (context.isPresent() && context.get().validCas(cas)) {
            return Optional.of(context.get().command());
        } else {
            return Optional.empty();
        }
    }

    public Optional<SetCommand> get(Command cmd) {
        return this.get(cmd.key);
    }

    public void delete(Command cmd) {
        var removed = this.cache.remove(cmd.key);
        if (removed != null) {
            this.lruCache.remove(cmd.key);
            this.memoryInBytes -= removed.bytes();
        }
    }

    public boolean hasMaxMemoryReached() {
        return this.memoryInBytes > this.maxMemoryBytes;
    }
}
