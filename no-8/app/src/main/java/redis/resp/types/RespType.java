package redis.resp.types;

import java.util.Optional;

import redis.resp.RespException;
import redis.resp.RespScanner;
import redis.resp.cache.ExpirationPolicy;
import redis.resp.commands.RespCommandException;

public abstract class RespType<T> {
    public static final RespType[] EMPTY_TYPE_ARRAY = new RespType[0];

    public final T value;

    protected RespType(T value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean isPresent() {
        return !this.isEmpty();
    }

    public Long intValue() throws RespException {
        throw new RespException("int value not allowed for this type");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RespType other = (RespType) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    public String toRespString() {
        var builder = new StringBuilder();
        toRespString(builder);
        return builder.toString();
    }

    public String toRespEscapedString() {
        return RespScanner.convertNewLinesBack(toRespString());
    }

    public abstract void toRespString(StringBuilder buffer);

    public boolean isCommandType() {
        return false;
    }

    public boolean isSubFunction(String subFunction) {
        return false;
    }

    public Optional<String> getSubFunction() {
        return Optional.empty();
    }

    // if expiration policy changes the return value, return this - if not keep
    // standard algorithm with nil and old value
    public RespType valueForSetOperation(Optional<RespType> oldValue, ExpirationPolicy expirationPolicy)
            throws RespCommandException {
        var newValueOrEmpty = expirationPolicy.changedValueForSetOperation(this, oldValue);
        if (newValueOrEmpty.isPresent()) {
            return newValueOrEmpty.get();
        } else {
            if (oldValue.isEmpty()) {
                return this;
            } else {
                return oldValue.get();
            }
        }
    }

}
