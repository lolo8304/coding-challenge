package redis.resp.commands.library;

import java.util.Optional;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.types.RespError;
import redis.resp.types.RespNull;
import redis.resp.types.RespSortedMap;
import redis.resp.types.RespType;

public class CmdGet extends RespLibraryFunction {

    public CmdGet(RespCommandLibrary library) {
        super(library);
    }

    @Override
    public String commandName() {
        return "GET";
    }

    @Override
    public RespResponse execute(RespRequest request) {
        if (request.getArgumentsCount(1) != 1) {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'get' command"));
        }
        Optional<String> key = request.command.getValue(1);
        if (key.isPresent()) {
            Optional<RespType> returnValue = request.cache.get(key.get());
            if (returnValue.isPresent()) {
                return new RespResponse(returnValue.get());
            } else {
                return new RespResponse(RespNull.NULL);
            }
        } else {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'get' command"));
        }
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args = new RespSortedMap().put("name", "key").put("type", "key");
        return new RespSortedMap()
                .put("summary", "Get the value of a key")
                .put("since", "1.0.0")
                .put("group", "string")
                .put("complexity", "O(1)")
                .put("arguments", args);
    }
}
