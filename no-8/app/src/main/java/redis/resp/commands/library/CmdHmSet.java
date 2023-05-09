package redis.resp.commands.library;

import java.util.Optional;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespArray;
import redis.resp.types.RespError;
import redis.resp.types.RespSimpleString;
import redis.resp.types.RespSortedMap;

public class CmdHmSet extends RespLibraryFunction {

    public CmdHmSet(RespCommandLibrary library) {
        super(library);
    }

    @Override
    public String commandName() {
        return "HMSET";
    }

    @Override
    public RespResponse execute(RespRequest request) throws RespCommandException {
        Optional<String> key = request.command.getValue(1);
        var args = request.getArguments(2);
        if (key.isPresent() && args.isPresent()) {
            var map = args.get().arrayToMap();
            request.cache.set(key.get(), map);
            return new RespResponse(new RespSimpleString("OK"));
        } else {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'hmset' command"));
        }
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args1V = new RespSortedMap().put("name", "field").put("type", "string");
        var args2V = new RespSortedMap().put("name", "value").put("type", "string");
        var argsV = new RespArray(args1V, args2V);

        var args1 = new RespSortedMap().put("name", "key").put("type", "key").put("key_spec_index", 0);
        var args2 = new RespSortedMap().put("name", "field_value").put("type", "block").put("arguments", argsV);
        var args = new RespArray(args1, args2);
        return new RespSortedMap()
                .put("summary", "Set multiple hash fields to multiple values")
                .put("since", "2.0.0")
                .put("group", "hash")
                .put("complexity", "O(N) where N is the number of fields being set.")
                .put("arguments", args);
    }
}
