package redis.resp.types;

public class RespError extends RespType<String> {

    public RespError(String value) {
        super(value);
    }

    @Override
    public void toRespString(StringBuilder buffer) {
        buffer.append('-').append(value).append("\r\n");
    }
}
