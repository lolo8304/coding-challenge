package redis.resp.commands.library;

import java.util.Optional;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespArray;
import redis.resp.types.RespBulkString;
import redis.resp.types.RespError;
import redis.resp.types.RespSimpleString;
import redis.resp.types.RespSortedMap;

public class CmdConfig extends RespLibraryFunction {

    protected CmdConfig(RespCommandLibrary library) {
        super("CONFIG", new String[] { "GET" }, library);
    }

    @Override
    public RespResponse execute(RespRequest request) throws RespCommandException {
        throw new RespCommandException("function CONFIG not supported");
    }

    @Override
    public RespResponse execute(RespRequest request, String subFunction) throws RespCommandException {
        switch (subFunction) {
            case "get":
                return executeSubFunctionGet(request);

            default:
                return super.execute(request, subFunction);
        }
    }

    private RespResponse executeSubFunctionGet(RespRequest request) {
        var arg = request.command.getValue(1);
        if (arg.isPresent()) {
            return new RespResponse(new RespSortedMap().put("save", "3600 1 300 100 60 10000"));
        } else {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'config|get' command"));
        }
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args = new RespSortedMap().put("name", "parameter").put("type", "block");
        return new RespSortedMap()
                .put("summary", "A container for server configuration commands")
                .put("since", "2.0.0")
                .put("group", "server")
                .put("complexity", "Depends on subcommand")
                .put("arguments", args);
    }
}
