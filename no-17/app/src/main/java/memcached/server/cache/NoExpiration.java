package memcached.server.cache;

import memcached.commands.SetCommand;

public class NoExpiration extends ExpirationPolicy {

    @Override
    public boolean tryApplyToCacheContext(SetCommand command) {
        return true;
    }

}
