package redis.resp.types;

import java.util.Optional;

import redis.resp.IRespBuilder;
import redis.resp.RespException;
import redis.resp.cache.ExpirationPolicy;
import redis.resp.commands.RespCommandException;

public abstract class RespType<T> implements IRespBuilder {
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

    public String stringValue() throws RespException {
        throw new RespException("String value not allowed for this type");
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
        return IRespBuilder.toRespString(this);
    }

    public String toRespEscapedString() {
        return IRespBuilder.toRespEscapedString(this);
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

    public static Long getLong(RespType obj) throws RespException {
        if (obj instanceof RespInteger) {
            return obj.intValue();
        } else {
            return Long.valueOf(obj.stringValue());
        }
    }

    public Integer getInteger() throws RespException {
        return getInteger(this);
    }

    public String getString() throws RespException {
        return getString(this);
    }

    public Long getLong() throws RespException {
        return getLong(this);
    }

    public static Integer getInteger(RespType obj) throws RespException {
        if (obj instanceof RespInteger) {
            return Integer.valueOf((int) obj.intValue().longValue());
        } else {
            return Integer.valueOf(obj.stringValue());
        }
    }

    public static String getString(RespType obj) throws RespException {
        return obj.stringValue();
    }

    public RespType incr(Long by) throws RespException {
        Long number = getLong(this);
        number += by;
        return new RespBulkString(number);
    }

    public RespType decr(Long by) throws RespException {
        return incr(-by);
    }

    @Override
    public RespType toRespType() {
        return this;
    }

    @Override
    public void loadFrom(RespArray data) throws RespException {
        // TODO Auto-generated method stub

    }
}
