package redis.resp.commands.library;

import java.util.Optional;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespArray;
import redis.resp.types.RespError;
import redis.resp.types.RespNull;
import redis.resp.types.RespSimpleString;
import redis.resp.types.RespSortedMap;

public class CmdHmGet extends RespLibraryFunction {

    public CmdHmGet(RespCommandLibrary library) {
        super(library);
    }

    @Override
    public String commandName() {
        return "HMGET";
    }

    @Override
    public RespResponse execute(RespRequest request) throws RespCommandException {
        Optional<String> key = request.command.getValue(1);
        Optional<String> field = request.command.getValue(2);
        if (key.isPresent() && field.isPresent()) {
            Optional<RespSortedMap> returnValue = request.cache.get(key.get());
            if (returnValue.isPresent()) {
                Optional<RespSimpleString> value = returnValue.get().get(field.get());
                if (value.isPresent()) {
                    return new RespResponse(value.get());
                } else {
                    return new RespResponse(RespNull.NULL);
                }
            } else {
                return new RespResponse(RespNull.NULL);
            }
        } else {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'hmset' command"));
        }
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args1 = new RespSortedMap().put("name", "key").put("type", "key").put("key_spec_index", 0);
        var args2 = new RespSortedMap().put("name", "field").put("type", "string");
        var args = new RespArray(args1, args2);
        return new RespSortedMap()
                .put("summary", "Set multiple hash fields to multiple values")
                .put("since", "2.0.0")
                .put("group", "hash")
                .put("complexity", "O(N) where N is the number of fields being set.")
                .put("arguments", args);
    }
}
