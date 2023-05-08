package redis.resp.commands.library;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespArray;
import redis.resp.types.RespSimpleString;
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
        if (subFunction.equals("docs")) {
            return executeSubFunctionDocs(request);
        } else {
            return super.execute(request, subFunction);
        }
    }

    private RespResponse executeSubFunctionDocs(RespRequest request) {
        return new RespResponse(RespCommandLibrary.INSTANCE.getCommandDocs(request.getArguments(2)));
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var argsCmd1 = new RespSortedMap().put("name", "command-name").put("type", "string").put("flags",
                new RespArray(new RespSimpleString("optional"), new RespSimpleString("multiple")));
        var argsCmd = new RespArray(argsCmd1);

        var argsDocs = new RespSortedMap()
                .put("summary", "Get array of specific Redis command documentation")
                .put("since", "7.0.0")
                .put("group", "server")
                .put("complexity", "O(N) where N is the number of commands to look up")
                .put("arguments", argsCmd);

        var args = new RespSortedMap().put("command|docs", argsDocs);

        return new RespSortedMap()
                .put("summary", "Get array of Redis command details")
                .put("since", "2.8.13")
                .put("group", "server")
                .put("complexity", "O(1)")
                .put("subcommands", args);
    }

}
