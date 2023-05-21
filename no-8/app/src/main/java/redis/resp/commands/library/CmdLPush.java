package redis.resp.commands.library;

import java.util.ArrayList;
import java.util.Optional;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespArray;
import redis.resp.types.RespError;
import redis.resp.types.RespInteger;
import redis.resp.types.RespSortedMap;
import redis.resp.types.RespType;

public class CmdLPush extends RespLibraryFunction {

    public CmdLPush(RespCommandLibrary library) {
        super(library);
    }

    @Override
    public String commandName() {
        return "LPUSH";
    }

    protected RespArray add(RespArray value, ArrayList<RespType> toAdd) {
        return value.lpush(toAdd);
    }

    @Override
    public RespResponse execute(RespRequest request) throws RespCommandException {
        if (request.getArgumentsCount(1) < 2) {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'lpush' command"));
        }
        var index = 1;
        Optional<String> key = request.command.getValue(index++);
        if (key.isPresent()) {
            Optional<RespArray> value = request.cache.get(key.get());
            var toAdd = new ArrayList<RespType>();
            Optional<RespType> option = request.command.get(index++);
            while (option.isPresent()) {
                toAdd.add(option.get());
                option = request.command.get(index++);
            }
            if (value.isEmpty()) {
                value = Optional.of(RespArray.EMPTY_ARRAY);
            }
            var newArray = this.add(value.get(), toAdd);
            request.cache.set(key.get(), newArray);
            return new RespResponse(new RespInteger(newArray.length));
        } else {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'lpush' command"));
        }
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args = new RespSortedMap().put("name", "key").put("type", "key");
        return new RespSortedMap()
                .put("summary",
                        "Prepend one or multiple elements to a list")
                .put("since", "1.0.0")
                .put("group", "list")
                .put("complexity",
                        "O(1) for each element added, so O(N) to add N elements when the command is called with multiple arguments.")

                .put("arguments", args);
    }
}
