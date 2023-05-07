package redis.resp.types;

public class RespNull extends RespBulkString {
    public static final RespNull NULL = new RespNull();

    public RespNull() {
        super(-1, "");
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void toRespString(StringBuilder buffer) {
        buffer.append("$-1\r\n");
    }
}
