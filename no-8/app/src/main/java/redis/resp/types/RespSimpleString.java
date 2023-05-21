package redis.resp.types;

import java.util.Optional;

import redis.resp.cache.ExpirationPolicy;
import redis.resp.commands.RespCommandException;

public class RespSimpleString extends RespType<String> {

    public RespSimpleString(String value) {
        super(value);
    }

    @Override
    public void toRespString(StringBuilder buffer) {
        buffer.append('+').append(value).append("\r\n");
    }

    // Simple string reply: OK if SET was executed correctly.
    @Override
    public RespType valueForSetOperation(Optional<RespType> oldValue, ExpirationPolicy expirationPolicy)
            throws RespCommandException {
        var newValueOrEmpty = expirationPolicy.changedValueForSetOperation(this, oldValue);
        if (newValueOrEmpty.isPresent()) {
            return newValueOrEmpty.get();
        } else {
            return new RespSimpleString("OK");
        }
    }

}
