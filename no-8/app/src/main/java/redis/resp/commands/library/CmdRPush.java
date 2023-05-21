package redis.resp.commands.library;

import java.util.ArrayList;

import redis.resp.types.RespArray;
import redis.resp.types.RespSortedMap;
import redis.resp.types.RespType;

public class CmdRPush extends CmdLPush {

    public CmdRPush(RespCommandLibrary library) {
        super(library);
    }

    @Override
    public String commandName() {
        return "RPUSH";
    }

    @Override
    protected RespArray add(RespArray value, ArrayList<RespType> toAdd) {
        return value.rpush(toAdd);
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args = new RespSortedMap().put("name", "key").put("type", "key");
        return new RespSortedMap()
                .put("summary",
                        "Append one or multiple elements to a list")
                .put("since", "1.0.0")
                .put("group", "list")
                .put("complexity",
                        "O(1) for each element added, so O(N) to add N elements when the command is called with multiple arguments.")

                .put("arguments", args);
    }
}
