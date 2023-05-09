package redis.resp.types;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

public class RespSortedMap extends RespType<List<RespSortedMap.Entry>> {

    private final Hashtable<String, RespType> map;

    public RespSortedMap(List<RespSortedMap.Entry> value) {
        super(value);
        this.map = new Hashtable<String, RespType>();
        initMap(value);
    }

    public RespSortedMap() {
        this(new ArrayList<>());
    }

    private void initMap(List<Entry> values) {
        for (Entry entry : values) {
            this.map.put(entry.key, entry.value);
        }
    }

    public RespSortedMap put(String key, RespType value) {
        var entry = new Entry(key, value);
        this.value.add(entry);
        this.map.put(entry.key, entry.value);
        return this;
    }

    public RespSortedMap put(String key, String value) {
        var entry = new Entry(key, new RespBulkString(value));
        this.value.add(entry);
        this.map.put(entry.key, entry.value);
        return this;
    }

    public RespSortedMap put(String key, int value) {
        var entry = new Entry(key, new RespBulkString(value));
        this.value.add(entry);
        this.map.put(entry.key, entry.value);
        return this;
    }

    public <T extends RespType> Optional<T> get(String key) {
        T found = (T) this.map.get(key);
        return found == null ? Optional.empty() : Optional.of(found);
    }

    @Override
    public void toRespString(StringBuilder buffer) {
        this.mapToArray().toRespString(buffer);
    }

    public RespArray mapToArray() {
        var list = new ArrayList<RespType>();
        for (Entry entry : value) {
            list.add(new RespBulkString(entry.key));
            list.add(entry.value);
        }
        return new RespArray(list);
    }

    public static class Entry {
        public final String key;
        public final RespType value;

        public Entry(String key, RespType value) {
            this.key = key;
            this.value = value;
        }
    }
}
