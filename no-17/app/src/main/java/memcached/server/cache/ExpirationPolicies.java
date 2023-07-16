package memcached.server.cache;

import java.util.ArrayList;
import java.util.List;

public class ExpirationPolicies extends ExpirationPolicy {

    private List<ExpirationPolicy> expirationPolicies;

    public ExpirationPolicies(ExpirationPolicy expirationPolicy) {
        this.expirationPolicies = new ArrayList<>();
        this.add(expirationPolicy);
    }

    @Override
    public boolean tryApplyToCacheContext(CacheContext context) {
        var result = true;
        for (ExpirationPolicy expirationPolicy : expirationPolicies) {
            // false is stronger, and must call all in the pipeline
            result = expirationPolicy.tryApplyToCacheContext(context) && result;
        }
        return result;
    }

    public ExpirationPolicy add(ExpirationPolicy nextExpirationPolicy) {
        this.expirationPolicies.add(nextExpirationPolicy);
        return this;
    }

}
