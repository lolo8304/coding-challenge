package redis.resp.types;

import java.util.Optional;

public class RespSimpleString extends RespType<String> {

    public RespSimpleString(String value) {
        super(value);
    }

    @Override
    public void toRespString(StringBuilder buffer) {
        buffer.append('+').append(value).append("\r\n");
    }

    // Simple string reply: OK if SET was executed correctly.
    public RespType valueForSetOperation(Optional<RespType> oldValue) {
        return new RespSimpleString("OK");
    }

}
