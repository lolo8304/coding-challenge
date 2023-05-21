package redis.resp.commands.library;

import java.util.Optional;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.types.RespError;
import redis.resp.types.RespInteger;
import redis.resp.types.RespSortedMap;

public class CmdExists extends RespLibraryFunction {

    public CmdExists(RespCommandLibrary library) {
        super(library);
    }

    @Override
    public String commandName() {
        return "EXISTS";
    }

    @Override
    public RespResponse execute(RespRequest request) {
        if (request.getArgumentsCount(1) < 1) {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'exists' command"));
        }
        var index = 1;
        Optional<String> key = request.command.getValue(index++);
        if (key.isPresent()) {
            var counter = 0;
            while (key.isPresent()) {
                if (request.cache.get(key.get()).isPresent()) {
                    counter++;
                }
                key = request.command.getValue(index++);
            }
            return new RespResponse(new RespInteger(counter));
        } else {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'exists' command"));
        }
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args = new RespSortedMap().put("name", "key").put("type", "key");
        return new RespSortedMap()
                .put("summary", "Determine if a key exists")
                .put("since", "1.0.0")
                .put("group", "string")
                .put("complexity", "O(N) where N is the number of keys to check.")
                .put("arguments", args);
    }
}
