package redis.resp.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import redis.resp.RespException;

public class RespArray extends RespType<RespType[]> {

    public final static RespArray EMPTY_ARRAY = new RespArray(RespType.EMPTY_TYPE_ARRAY);

    public final Long length;

    public RespArray(Long length, RespType[] value) {
        super(value);
        this.length = length;
    }

    public RespArray(Integer length, RespType[] value) {
        super(value);
        this.length = Long.valueOf(length);
    }

    public RespArray(RespType... value) {
        super(value);
        this.length = Long.valueOf(value.length);
    }

    public RespArray(String... value) {
        super(Arrays.asList(value).stream().map(x -> new RespBulkString(x)).toArray(RespBulkString[]::new));
        this.length = Long.valueOf(value.length);
    }

    public RespArray(List<RespType> value) {
        super(value.toArray(RespType[]::new));
        this.length = Long.valueOf(value.size());
    }

    @Override
    public void toRespString(StringBuilder buffer) {
        buffer.append("*").append(this.length).append("\r\n");
        for (RespType respType : value) {
            respType.toRespString(buffer);
        }
    }

    public RespType first() {
        return this.get(0).get();
    }

    public Optional<RespType> second() {
        return this.get(1);
    }

    public Optional<RespType> get(int index) {
        if (value.length > index) {
            return Optional.of(value[index]);
        } else {
            return Optional.empty();
        }
    }

    public RespArray lpush(List<RespType> value) {
        var newArray = new ArrayList<>(value);
        newArray.addAll(Arrays.asList(this.value));
        return new RespArray(newArray);
    }

    public RespArray rpush(List<RespType> value) {
        var newArray = new ArrayList<>(Arrays.asList(this.value));
        newArray.addAll(value);
        return new RespArray(newArray);
    }

    @Override
    public boolean isCommandType() {
        return true;
    }

    public Optional<String> getSubFunction() {
        var subFunctionElement = this.second();
        return subFunctionElement.isPresent() && !subFunctionElement.get().isCommandType()
                ? subFunctionElement.get().getSubFunction()
                : Optional.empty();
    }

    public boolean hasSubFunction(String subFunction) {
        var subFunctionElement = this.second();
        return (subFunctionElement.isPresent() && subFunctionElement.get().isSubFunction(subFunction));
    }

    public boolean contains(String filter) {
        for (RespType respType : this.value) {
            if (respType.value.equals(filter)) {
                return true;
            }
        }
        return false;
    }

    public RespSortedMap arrayToMap() {
        var entries = new ArrayList<RespSortedMap.Entry>();
        for (int i = 0; i < this.value.length; i++) {
            var key = (String) this.value[i++].value;
            var value = this.value[i];
            entries.add(new RespSortedMap.Entry(key, value));
        }
        return new RespSortedMap(entries);
    }

    @Override
    public void loadFrom(RespArray data) throws RespException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadFrom'");
    }
}
