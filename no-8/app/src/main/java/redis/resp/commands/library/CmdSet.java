package redis.resp.commands.library;

import java.util.Optional;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.types.RespArray;
import redis.resp.types.RespError;
import redis.resp.types.RespSimpleString;
import redis.resp.types.RespSortedMap;

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
        Optional<String> stringValue = request.command.getValue(2);
        if (key.isPresent() && stringValue.isPresent()) {
            // convert from BulkString to SimpleString -x option is not available
            Optional<String> option = request.getString(3);
            if (option.isPresent()) {
                switch (option.get().toLowerCase()) {
                    case "ex":
                        Optional<Integer> expireTimeInS = request.getInteger(4);
                        break;
                    case "px":
                        Optional<Integer> expireTimeInMs = request.getInteger(4);
                        break;
                    case "exat":
                        Optional<Integer> expireInS = request.getInteger(4);
                        break;
                    case "pxat":
                        Optional<Integer> expireInMs = request.getInteger(4);
                        break;
                    case "nx":
                        break;
                    case "xx":
                        break;
                    case "keepttl":
                        break;
                    case "get":
                        break;
                    default:
                        break;

                }
            }
            var returnValue = request.cache.set(key.get(), new RespSimpleString(stringValue.get()));
            return new RespResponse(returnValue);
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
