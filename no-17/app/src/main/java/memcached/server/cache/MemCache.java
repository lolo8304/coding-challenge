package memcached.server.cache;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Optional;

import memcached.commands.Command;
import memcached.commands.SetCommand;

public class MemCache {
    private Dictionary<String, CacheContext> cache;

    public MemCache() {
        this.cache = new Hashtable<>();
    }

    public Optional<String> set(SetCommand cmd) {
        if (cmd.isValidToAddToCache(this)) {
            this.cache.put(cmd.key, new CacheContext(this, cmd));
            return Optional.of(cmd.data.data);
        } else {
            return Optional.empty();
        }
    }

    public Optional<SetCommand> get(String key) {
        var value = this.cache.get(key);
        if (value != null) {
            if (value.isAlive()) {
                return Optional.of(value.command());
            } else {
                value.evict();
                return Optional.empty();
            }
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
