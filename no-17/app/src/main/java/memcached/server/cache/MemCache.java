package memcached.server.cache;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Optional;

import memcached.commands.Command;
import memcached.commands.SetCommand;
import memcached.commands.ValidationException;

public class MemCache {
    private Dictionary<String, SetCommand> cache;

    public MemCache() {
        this.cache = new Hashtable<>();
    }

    public Optional<String> set(SetCommand cmd) throws ValidationException {
        this.cache.put(cmd.key, cmd);
        return Optional.of(cmd.data.data);
    }

    public Optional<SetCommand> get(String key) {
        var value = this.cache.get(key);
        if (value != null) {
            if (value.isAlive()) {
                return Optional.of(value);
            } else {
                value.evict(this);
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
        this.cache.remove(cmd);
    }
}
