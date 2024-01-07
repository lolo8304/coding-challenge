package json.model;

import java.util.ArrayList;
import java.util.List;

public class JArray extends JValue {

    private final List<JValue> values = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JArray other = (JArray) obj;
        return other.toString().equals(this.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public JsonBuilder serialize(JsonBuilder builder) {
        builder.append('[').newLineIfNotCompact().indentPlus();
        var second = false;
        for (JValue jValue : values) {
            if (second) { builder.append(", ").newLineIfNotCompact(); }
            jValue.serialize(builder);
            second = true;
        }
        builder.newLineIfNotCompact().indentMinus().append(']');
        return builder;
    }

    public void addValues(List<JValue> newValues) {
        this.values.addAll(newValues);
    }
    public void addValue(JValue newValues) {
        this.values.add(newValues);
    }

    @Override
    public Object value() {
        return this;
    }

    public JValue[] values () {
        return this.values.toArray(JValue[]::new);
    }


    @Override
    public JValue get(int index) {
        return this.values.get(index);
    }

    @Override
    public JValue get(String key) {
        return this.values.get(this.indexFromString(key));
    }

    private int indexFromString(String key) {
        try {
            return Integer.parseInt(key);
        } catch (Exception e) {
            throw new IllegalArgumentException("Key '"+key+"' is not a number for index access in array");
        }
    }
}
