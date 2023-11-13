package json.model;

import json.JsonParserException;

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
        builder.append('[').indentPlus().indentPlus();
        var second = false;
        for (JValue jValue : values) {
            if (second) { builder.append(", ").newLineIfNotCompact(); }
            jValue.serialize(builder);
            second = true;
        }
        builder.newLineIfNotCompact().indentMinus().append(']').indentMinus();
        return builder;
    }

    public List<JValue> addValues(List<JValue> newValues) {
        this.values.addAll(newValues);
        return this.values;
    }

    @Override
    public Object value() {
        return this;
    }
    

}
