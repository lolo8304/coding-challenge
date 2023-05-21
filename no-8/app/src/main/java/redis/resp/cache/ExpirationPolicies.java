package redis.resp.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import redis.resp.cache.RedisCache.RedisCacheContext;
import redis.resp.commands.RespCommandException;
import redis.resp.types.RespType;

public class ExpirationPolicies extends ExpirationPolicy {

    private List<ExpirationPolicy> expirationPolicies;

    public ExpirationPolicies(ExpirationPolicy expirationPolicy) {
        this.expirationPolicies = new ArrayList<>();
        this.add(expirationPolicy);
    }

    @Override
    public boolean tryApplyToCacheContext(RespType value, RedisCacheContext context) {
        var result = true;
        for (ExpirationPolicy expirationPolicy : expirationPolicies) {
            // false is stronger, and must call all in the pipeline
            result = expirationPolicy.tryApplyToCacheContext(value, context) && result;
        }
        return result;
    }

    public ExpirationPolicy add(ExpirationPolicy nextExpirationPolicy) {
        this.expirationPolicies.add(nextExpirationPolicy);
        return this;
    }

    @Override
    public Optional<RespType> changedValueForSetOperation(RespType newValue, Optional<RespType> oldValue)
            throws RespCommandException {
        Optional<RespType> result = Optional.empty();
        for (ExpirationPolicy expirationPolicy : expirationPolicies) {
            result = expirationPolicy.changedValueForSetOperation(newValue, oldValue);
            if (result.isPresent()) {
                return result;
            }
        }
        return result;
    }
}
