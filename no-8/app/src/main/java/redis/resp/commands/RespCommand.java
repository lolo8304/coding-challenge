package redis.resp.commands;

import java.util.Optional;

import redis.resp.types.RespArray;
import redis.resp.types.RespType;

public class RespCommand {
    public final RespArray array;

    public RespCommand(RespArray array) {
        this.array = array;
    }

    public Optional<RespType> get(int index) {
        return array.get(index);
    }

    public <T> Optional<T> getValue(int index) {
        var value = array.get(index);
        if (value.isPresent()) {
            return Optional.of((T) value.get().value);
        } else {
            return Optional.empty();
        }
    }

}
