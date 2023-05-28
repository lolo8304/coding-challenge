package redis.resp.commands.library;

import redis.resp.RespRequest;
import redis.resp.RespResponse;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespError;
import redis.resp.types.RespSimpleString;
import redis.resp.types.RespSortedMap;

public class CmdSave extends RespLibraryFunction {

    public CmdSave(RespCommandLibrary library) {
        super(library);
    }

    @Override
    public String commandName() {
        return "SAVE";
    }

    @Override
    public RespResponse execute(RespRequest request) throws RespCommandException {
        if (request.getArgumentsCount(1) != 0) {
            return new RespResponse(new RespError("ERR wrong number of arguments for 'save' command"));
        }
        try {
            request.cache.startBlockingOperation();
            this.saveToFile(request);
        } finally {
            request.cache.stopBlockingOperation();
        }
        return new RespResponse(new RespSimpleString("OK"));
    }

    private void saveToFile(RespRequest request) {
        request.cache.save();
    }

    @Override
    public RespSortedMap getCommandDocs() {
        var args = new RespSortedMap().put("name", "key").put("type", "key");
        return new RespSortedMap()
                .put("summary", "Synchronously save the dataset to disk")
                .put("since", "1.0.0")
                .put("group", "server")
                .put("complexity", "O(N) where N is the total number of keys in all databases")
                .put("arguments", args);
    }
}
