package redis.resp;

import java.util.ArrayList;
import java.util.Optional;

import redis.resp.cache.RedisCache;
import redis.resp.commands.RespCommand;
import redis.resp.types.RespArray;
import redis.resp.types.RespInteger;
import redis.resp.types.RespType;

public class RespRequest {

    public final RedisCache cache;
    public final RespCommand command;

    public RespRequest(RedisCache cache, RespCommand command) {
        this.cache = cache;
        this.command = command;
    }

    public String getFunction() {
        return (String) this.command.array.first().value;
    }

    public Optional<String> getSubFunction() {
        var subfunction = this.command.array.second();
        if (subfunction.isPresent()) {
            return Optional.of((String) subfunction.get().value);
        } else {
            return Optional.empty();
        }
    }

    public boolean hasSubFunction(String subFunction) {
        return this.command.array.hasSubFunction(subFunction);
    }

    public Optional<String> getString(int index) {
        return this.command.getValue(index);
    }

    public Optional<Integer> getInteger(int index) {
        var obj = this.command.get(index);
        if (obj.isPresent()) {
            if (obj.get() instanceof RespInteger) {
                Optional<Long> value = this.command.getValue(index);
                return Optional.of(Integer.valueOf((int) value.get().longValue()));
            } else {
                Optional<String> value = this.command.getValue(index);
                return Optional.of(Integer.valueOf(value.get()));
            }
        } else {
            return Optional.empty();
        }
    }

    public Optional<Long> getLong(int index) {
        var obj = this.command.get(index);
        if (obj.isPresent()) {
            if (obj.get() instanceof RespInteger) {
                return this.command.getValue(index);
            } else {
                Optional<String> value = this.command.getValue(index);
                return Optional.of(Long.valueOf(value.get()));
            }
        } else {
            return Optional.empty();
        }
    }

    public Optional<RespType> getArgument(int index) {
        return this.command.get(index);
    }

    public Optional<RespArray> getArguments() {
        return this.getArguments(1);
    }

    public int getArgumentsCount(int starting) {
        var array = this.getArguments(starting);
        return array.isEmpty() ? 0 : array.get().value.length;
    }

    public Optional<RespArray> getArguments(int starting) {
        var i = starting;
        var filters = new ArrayList<String>();
        var arguments = this.command.get(i++);
        while (arguments.isPresent()) {
            filters.add((String) arguments.get().value);
            arguments = this.command.get(i++);
        }
        Optional<RespArray> array = Optional.empty();
        if (!filters.isEmpty()) {
            array = Optional.of(new RespArray(filters.toArray(String[]::new)));
        }
        return array;
    }
}
