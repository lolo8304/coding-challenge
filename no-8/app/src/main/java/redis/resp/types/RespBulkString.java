package redis.resp.types;

import java.util.Optional;

public class RespBulkString extends RespType<String> {

    public final Long nofBytes;

    public RespBulkString(String value) {
        super(value);
        this.nofBytes = (long) value.length();
    }

    public RespBulkString(Integer value) {
        super(String.valueOf(value));
        this.nofBytes = (long) this.value.length();
    }

    public RespBulkString(Long nofBytes, String value) {
        super(value);
        this.nofBytes = nofBytes;
    }

    public RespBulkString(Integer nofBytes, String value) {
        super(value);
        this.nofBytes = nofBytes.longValue();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((nofBytes == null) ? 0 : nofBytes.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        RespBulkString other = (RespBulkString) obj;
        if (nofBytes == null) {
            if (other.nofBytes != null)
                return false;
        } else if (!nofBytes.equals(other.nofBytes))
            return false;
        return true;
    }

    @Override
    public void toRespString(StringBuilder buffer) {
        buffer.append('$').append(this.nofBytes).append("\r\n").append(value).append("\r\n");
    }

    @Override
    public boolean isSubFunction(String subFunction) {
        return this.value.equalsIgnoreCase(subFunction);
    }

    public Optional<String> getSubFunction() {
        return Optional.of(value);
    }

    // Bulk string reply: the old string value stored at key.
    public RespType valueForSetOperation(Optional<RespType> oldValue) {
        if (oldValue.isEmpty()) {
            return this;
        } else {
            return oldValue.get();
        }
    }

}
