package redis.resp.commands.library;

import java.util.ArrayList;
import java.util.Optional;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespArray;
import redis.resp.types.RespSortedMap;

public class CmdCommand extends RespLibraryFunction {

    protected CmdCommand(RespCommandLibrary library) {
        super("COMMAND", new String[] { "DOCS" }, library);
    }

    @Override
    public RespResponse execute(RespRequest request) throws RespCommandException {
        throw new RespCommandException("function COMMAND not supported");
    }

    @Override
    public RespResponse execute(RespRequest request, String subFunction) throws RespCommandException {
        switch (subFunction) {
            case "docs":
                return executeSubFunctionDocs(request);

            default:
                return super.execute(request, subFunction);
        }
    }

    private RespResponse executeSubFunctionDocs(RespRequest request) {
        return new RespResponse(RespCommandLibrary.INSTANCE.getCommandDocs(request.getArguments(2)));
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args = new RespSortedMap().put("name", "message").put("type", "string").put("flags",
                new RespArray("optional", "multiple"));
        return new RespSortedMap()
                .put("summary", "Set the string value of a key")
                .put("since", "1.0.0")
                .put("group", "string")
                .put("complexity", "O(1)")
                .put("arguments", args);
    }

}
