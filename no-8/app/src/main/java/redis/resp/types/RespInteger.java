package redis.resp.types;

import redis.resp.RespException;

public class RespInteger extends RespType<Long> {

    public RespInteger(Long value) {
        super(value);
    }

    public RespInteger(Integer value) {
        super((long) value);
    }

    @Override
    public Long intValue() {
        return this.value;
    }

    @Override
    public void toRespString(StringBuilder buffer) {
        buffer.append(':').append(value).append("\r\n");
    }

    @Override
    public String stringValue() throws RespException {
        return String.valueOf(this.value);
    }

}
