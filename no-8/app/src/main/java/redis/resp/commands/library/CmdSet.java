package redis.resp.commands.library;

import java.util.Optional;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.cache.ExpirationPolicy;
import redis.resp.cache.ExpireAt;
import redis.resp.cache.ExpireIn;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespArray;
import redis.resp.types.RespError;
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

    public RespResponse execute(RespRequest request) throws RespCommandException {
        Optional<String> key = request.command.getValue(1);
        Optional<RespType> objectValue = request.command.get(2);
        if (key.isPresent() && objectValue.isPresent()) {
            // convert from BulkString to SimpleString -x option is not available
            var index = 3;
            Optional<String> option = request.getString(index++);
            ExpirationPolicy setPolicy = ExpirationPolicy.NONE;
            while (option.isPresent()) {
                if (option.isPresent()) {
                    switch (option.get().toLowerCase()) {
                        case "ex":
                            Optional<Integer> expireTimeInS = request.getInteger(index++);
                            setPolicy = setPolicy.add(ExpireIn.seconds(expireTimeInS.get()));
                            break;
                        case "px":
                            Optional<Long> expireTimeInMs = request.getLong(index++);
                            setPolicy = setPolicy.add(ExpireIn.milliseconds(expireTimeInMs.get()));
                            break;
                        case "exat":
                            Optional<Integer> expireInS = request.getInteger(index++);
                            setPolicy = setPolicy.add(ExpireAt.seconds(expireInS.get()));
                            break;
                        case "pxat":
                            Optional<Long> expireInMs = request.getLong(index++);
                            setPolicy = setPolicy.add(ExpireAt.milliseconds(expireInMs.get()));
                            break;
                        case "nx":
                            setPolicy = setPolicy.add(ExpirationPolicy.SET_IF_NOT_EXISTS);
                            break;
                        case "xx":
                            setPolicy = setPolicy.add(ExpirationPolicy.SET_IF_EXISTS);
                            ;
                            break;
                        case "keepttl":
                            setPolicy = setPolicy.add(ExpirationPolicy.KEEP_TTL);
                            break;
                        case "get":
                            setPolicy = setPolicy.add(ExpirationPolicy.GET_POLICY);
                            break;
                        default:
                            return new RespResponse(new RespError("ERR syntax error argument '" + option.get() + "'"));
                    }
                }
                option = request.getString(index++);
            }
            var returnValue = request.cache.set(key.get(), objectValue.get(), setPolicy);
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
