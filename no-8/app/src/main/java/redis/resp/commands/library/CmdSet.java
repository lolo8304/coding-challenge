package redis.resp.commands.library;

import java.util.Optional;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.types.RespArray;
import redis.resp.types.RespError;
import redis.resp.types.RespSimpleString;
import redis.resp.types.RespSortedMap;
import redis.resp.types.RespType;

public class CmdSet extends RespLibraryFunction {

    public CmdSet(RespCommandLibrary library) {
        super(library);
    }

    @Override
    public String commandName() {
        return "SET";
    }

    @Override
    public RespResponse execute(RespRequest request) {
        Optional<String> key = request.command.getValue(1);
        Optional<RespType> value = request.command.get(2);
        if (key.isPresent() && value.isPresent()) {
            // convert from BulkString to SimpleString -x option is not available
            Optional<String> stringValue = request.command.getValue(2);
            if (stringValue.isPresent()) {
                var returnValue = request.cache.set(key.get(), new RespSimpleString(stringValue.get()));
                return new RespResponse(returnValue);
            } else {
                return new RespResponse(new RespError("ERR wrong number of arguments for 'set' command"));
            }
        } else {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'set' command"));
        }
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args1 = new RespSortedMap().put("name", "key").put("type", "key").put("key_spec_index", 0);
        var args2 = new RespSortedMap().put("name", "value").put("type", "string");
        var args = new RespArray(args1, args2);
        return new RespSortedMap()
                .put("summary", "Set the string value of a key")
                .put("since", "1.0.0")
                .put("group", "string")
                .put("complexity", "O(1)")
                .put("arguments", args);
    }
}
