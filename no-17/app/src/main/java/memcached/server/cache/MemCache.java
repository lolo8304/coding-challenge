package memcached.server.cache;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Optional;

import memcached.commands.Command;
import memcached.commands.SetCommand;
import memcached.commands.ValidationCode;

public class MemCache {
    private Dictionary<String, CacheContext> cache;

    public MemCache() {
        this.cache = new Hashtable<>();
    }

    public Optional<ValidationCode> set(SetCommand cmd) {
        var validationResult = cmd.isValidToAddToCache(this);
        if (validationResult.equals(ValidationCode.OK)) {
            var existingValue = this.getValidContext(cmd.key);
            if (existingValue.isPresent()) {
                return cmd.setToContext(existingValue.get());
            } else {
                this.cache.put(cmd.key, new CacheContext(this, cmd));
                return Optional.of(ValidationCode.STORED);
            }
        } else {
            return Optional.of(validationResult);
        }
    }

    private Optional<CacheContext> getValidContext(String key) {
        var value = this.cache.get(key);
        if (value != null) {
            if (value.isAlive()) {
                return Optional.of(value);
            } else {
                value.evict();
            }
        }
        return Optional.empty();
    }

    public Optional<ValidationCode> delete(String key) {
        var context = this.getValidContext(key);
        if (context.isPresent()) {
            context.get().evict();
            return Optional.of(ValidationCode.DELETED);
        } else {
            return Optional.of(ValidationCode.NOT_FOUND);
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
        this.cache.remove(cmd.key);
    }
}
