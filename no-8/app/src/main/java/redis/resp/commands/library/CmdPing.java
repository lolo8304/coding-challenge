package redis.resp.commands.library;

import java.util.Optional;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespArray;
import redis.resp.types.RespBulkString;
import redis.resp.types.RespSimpleString;
import redis.resp.types.RespSortedMap;

public class CmdPing extends RespLibraryFunction {

    public CmdPing(RespCommandLibrary library) {
        super(library);
    }

    @Override
    public String commandName() {
        return "PING";
    }

    @Override
    public RespResponse execute(RespRequest request) throws RespCommandException {
        Optional<String> message = request.command.getValue(1);
        if (message.isPresent()) {
            return new RespResponse(new RespBulkString(message.get()));
        } else {
            return new RespResponse(new RespBulkString("PONG"));
        }
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args = new RespSortedMap().put("name", "message").put("type", "string").put("flags",
                new RespArray(new RespSimpleString("optional")));
        return new RespSortedMap()
                .put("summary", "Ping the server")
                .put("since", "1.0.0")
                .put("group", "connection")
                .put("complexity", "O(1)")
                .put("arguments", args);
    }
}
