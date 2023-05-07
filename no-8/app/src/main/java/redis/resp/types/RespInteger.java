package redis.resp.types;

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
}
