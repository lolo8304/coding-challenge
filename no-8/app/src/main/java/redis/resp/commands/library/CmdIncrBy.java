package redis.resp.commands.library;

import redis.resp.RespException;
import redis.resp.RespRequest;
import redis.resp.types.RespSortedMap;

public class CmdIncrBy extends CmdIncr {

    public CmdIncrBy(RespCommandLibrary library) {
        super(library);
    }

    @Override
    public String commandName() {
        return "INCRBY";
    }

    @Override
    protected Long incrBy(RespRequest request) throws RespException {
        var by = request.command.get(2);
        if (by.isPresent()) {
            return by.get().getLong();
        } else {
            return 0L;
        }
    }

    @Override
    protected int numberOfArguments() {
        return 2;
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args = new RespSortedMap().put("name", "key").put("type", "key");
        return new RespSortedMap()
                .put("summary",
                        "Increment the integer value of a key by the given amount")
                .put("since", "1.0.0")
                .put("group", "string")
                .put("complexity", "O(1)")

                .put("arguments", args);
    }

}
