package redis.resp.types;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandOption<T> {
    public final String key;
    public final Optional<T> value;
    public final List<T> values;

    public CommandOption(String key) {
        this(key, Optional.empty());
    }

    public CommandOption(String key, Optional<T> value) {
        this.key = key;
        this.value = value;
        if (value.isPresent()) {
            this.values = Arrays.asList(value.get());
        } else {
            this.values = Arrays.asList();
        }
    }

    public CommandOption(String key, List<T> values) {
        this.key = key;
        this.value = values.size() == 1 ? Optional.of(values.get(0)) : Optional.empty();
        this.values = values;
    }

    public boolean isOption(String opt) {
        return this.key.equals(opt);
    }
}
