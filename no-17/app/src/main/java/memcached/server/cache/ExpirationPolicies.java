package memcached.server.cache;

import java.util.ArrayList;
import java.util.List;

import memcached.commands.SetCommand;

public class ExpirationPolicies extends ExpirationPolicy {

    private List<ExpirationPolicy> expirationPolicies;

    public ExpirationPolicies(ExpirationPolicy expirationPolicy) {
        this.expirationPolicies = new ArrayList<>();
        this.add(expirationPolicy);
    }

    @Override
    public boolean tryApplyToCacheContext(SetCommand command) {
        var result = true;
        for (ExpirationPolicy expirationPolicy : expirationPolicies) {
            // false is stronger, and must call all in the pipeline
            result = expirationPolicy.tryApplyToCacheContext(command) && result;
        }
        return result;
    }

    public ExpirationPolicy add(ExpirationPolicy nextExpirationPolicy) {
        this.expirationPolicies.add(nextExpirationPolicy);
        return this;
    }

}
