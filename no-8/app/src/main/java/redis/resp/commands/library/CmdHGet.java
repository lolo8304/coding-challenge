package redis.resp.commands.library;

public class CmdHGet extends CmdHmGet {

    public CmdHGet(RespCommandLibrary library) {
        super(library);
    }

    @Override
    public String commandName() {
        return "HGET";
    }
}
