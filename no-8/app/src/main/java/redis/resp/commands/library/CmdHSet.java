package redis.resp.commands.library;

public class CmdHSet extends CmdHmSet {

    public CmdHSet(RespCommandLibrary library) {
        super(library);
    }

    @Override
    public String commandName() {
        return "HSET";
    }
}
