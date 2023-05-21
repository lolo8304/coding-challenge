package redis.resp.cache;

import java.util.Optional;

import redis.resp.cache.RedisCache.RedisCacheContext;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespBulkString;
import redis.resp.types.RespNull;
import redis.resp.types.RespSimpleString;
import redis.resp.types.RespType;

public class GetPolicy extends ExpirationPolicy {

    @Override
    public boolean tryApplyToCacheContext(RespType value, RedisCacheContext context) {
        return true;
    }

    @Override
    public Optional<RespType> changedValueForSetOperation(RespType newValue, Optional<RespType> oldValue)
            throws RespCommandException {
        // from https://redis.io/commands/set/
        // GET -- Return the old string stored at key, or nil if key did not exist. An
        // error is returned and SET aborted if the value stored at key is not a string.
        if (oldValue.isPresent()) {
            if (oldValue.get() instanceof RespSimpleString || oldValue.get() instanceof RespBulkString) {
                return oldValue;
            } else {
                throw new RespCommandException("GET option in SET is not allowed because old value is not Bulk String");
            }
        }
        return Optional.of(RespNull.NULL);
    }

}
