package redis.resp.types;

import java.util.ArrayList;
import java.util.List;

public class RespSortedMap extends RespType<List<RespSortedMap.Entry>> {

    public RespSortedMap(List<RespSortedMap.Entry> value) {
        super(value);
    }

    public RespSortedMap() {
        super(new ArrayList<>());
    }

    public static class Entry {
        public final String key;
        public final RespType value;

        public Entry(String key, RespType value) {
            this.key = key;
            this.value = value;
        }
    }

    public RespSortedMap put(String key, RespType value) {
        var entry = new Entry(key, value);
        this.value.add(entry);
        return this;
    }

    public RespSortedMap put(String key, String value) {
        var entry = new Entry(key, new RespBulkString(value));
        this.value.add(entry);
        return this;
    }

    public RespSortedMap put(String key, int value) {
        var entry = new Entry(key, new RespBulkString(value));
        this.value.add(entry);
        return this;
    }

    @Override
    public void toRespString(StringBuilder buffer) {
        this.mapToArray().toRespString(buffer);
    }

    private RespArray mapToArray() {
        var list = new ArrayList<RespType>();
        for (Entry entry : value) {
            list.add(new RespBulkString(entry.key));
            list.add(entry.value);
        }
        return new RespArray(list);
    }

}
