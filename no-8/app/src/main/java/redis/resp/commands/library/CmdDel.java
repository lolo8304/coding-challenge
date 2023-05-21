package redis.resp.commands.library;

import java.util.Optional;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespError;
import redis.resp.types.RespInteger;
import redis.resp.types.RespSortedMap;

public class CmdDel extends RespLibraryFunction {

    public CmdDel(RespCommandLibrary library) {
        super(library);
    }

    @Override
    public String commandName() {
        return "DEL";
    }

    @Override
    public RespResponse execute(RespRequest request) throws RespCommandException {
        if (request.getArgumentsCount(1) < 1) {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'del' command"));
        }
        var index = 1;
        Optional<String> key = request.command.getValue(index++);
        if (key.isPresent()) {
            var counter = 0;
            while (key.isPresent()) {
                var deleted = request.cache.del(key.get());
                if (deleted) {
                    counter++;
                }
                key = request.command.getValue(index++);
            }
            return new RespResponse(new RespInteger(counter));
        } else {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'del' command"));
        }
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args = new RespSortedMap().put("name", "key").put("type", "key");
        return new RespSortedMap()
                .put("summary", "Removes the specified keys. A key is ignored if it does not exist")
                .put("since", "1.0.0")
                .put("group", "string")
                .put("complexity",
                        "O(N) where N is the number of keys that will be removed. When a key to remove holds a value other than a string, the individual complexity for this key is O(M) where M is the number of elements in the list, set, sorted set or hash. Removing a single key that holds a string value is O(1).")
                .put("arguments", args);
    }
}
