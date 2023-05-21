package redis.resp.commands.library;

import redis.resp.RespException;
import redis.resp.RespRequest;
import redis.resp.types.RespSortedMap;

public class CmdDecr extends CmdIncr {

    public CmdDecr(RespCommandLibrary library) {
        super(library);
    }

    @Override
    public String commandName() {
        return "DECR";
    }

    @Override
    protected Long incrBy(RespRequest request) throws RespException {
        return -1L;
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args = new RespSortedMap().put("name", "key").put("type", "key");
        return new RespSortedMap()
                .put("summary",
                        "Decrement the integer value of a key by oneIncrement the integer value of a key by one")
                .put("since", "1.0.0")
                .put("group", "string")
                .put("complexity", "O(1)")

                .put("arguments", args);
    }
}
