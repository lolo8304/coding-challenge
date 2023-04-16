package json.model;

import java.util.ArrayList;
import java.util.List;

import json.JsonParserException;

public class JArray extends JValue {

    private List<JValue> values = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JObject other = (JObject) obj;
        return other.toString().equals(this.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        var buffer = new StringBuilder();
        buffer.append('[');
        var second = false;
        for (JValue jValue : values) {
            if (second) { buffer.append(','); }
            buffer.append(jValue.toString());
            second = true;
        }
        buffer.append(']');
        return buffer.toString();
    }

    public List<JValue> addValues(List<JValue> newValues) throws JsonParserException {
        this.values.addAll(newValues);
        return this.values;
    }

    @Override
    public Object value() {
        return this;
    }
    

}
