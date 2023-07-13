package memcached.server.cache;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Optional;

import memcached.commands.Command;
import memcached.commands.DataCommand;

public class MemCache {
    private Dictionary<String, DataCommand> cache;

    public MemCache() {
        this.cache = new Hashtable<>();
    }

    public Optional<String> set(DataCommand cmd) {
        this.cache.put(cmd.key, cmd);
        return Optional.of(cmd.data.data);
    }

    public Optional<DataCommand> get(String key) {
        var value = this.cache.get(key);
        if (value != null) {
            return Optional.of(value);
        } else {
            return Optional.empty();
        }
    }

    public Optional<DataCommand> get(Command cmd) {
        return this.get(cmd.key);
    }
}
