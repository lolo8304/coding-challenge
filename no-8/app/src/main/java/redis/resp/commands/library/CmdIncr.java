package redis.resp.commands.library;

import java.util.Optional;

import redis.resp.RespException;
import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespBulkString;
import redis.resp.types.RespError;
import redis.resp.types.RespInteger;
import redis.resp.types.RespSortedMap;
import redis.resp.types.RespType;

public class CmdIncr extends RespLibraryFunction {

    public CmdIncr(RespCommandLibrary library) {
        super(library);
    }

    @Override
    public String commandName() {
        return "INCR";
    }

    protected Long incrBy(RespRequest request) throws RespException {
        return 1L;
    }

    protected int numberOfArguments() {
        return 1;
    }

    @Override
    public RespResponse execute(RespRequest request) throws RespCommandException {
        if (request.getArgumentsCount(1) != this.numberOfArguments()) {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'incr' command"));
        }
        Optional<String> key = request.command.getValue(1);
        if (key.isPresent()) {
            Optional<RespType> returnValue = request.cache.get(key.get());
            if (returnValue.isEmpty()) {
                returnValue = Optional.of(new RespBulkString(0));
            }
            try {
                RespType incrValue = returnValue.get().incr(this.incrBy(request));
                request.cache.set(key.get(), incrValue);
                return new RespResponse(new RespInteger(incrValue.getLong()));
            } catch (NumberFormatException | RespException e) {
                return new RespResponse(new RespError("ERR value is not an integer or out of range"));
            }
        } else {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'incr' command"));
        }
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args = new RespSortedMap().put("name", "key").put("type", "key");
        return new RespSortedMap()
                .put("summary",
                        "Increment the integer value of a key by oneIncrement the integer value of a key by one")
                .put("since", "1.0.0")
                .put("group", "string")
                .put("complexity", "O(1)")

                .put("arguments", args);
    }
}
