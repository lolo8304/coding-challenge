package jq.parser;

import json.model.JArray;
import json.model.JValue;

import java.util.function.Consumer;

public class JsonQueryNodeIndexed extends JsonQueryNode{
    public JsonQueryNodeIndexed(JsonQueryParser.TokenValue value) {
        super(value);
    }

    public JsonQueryNodeIndexed(JsonQueryParser.TokenValue value, JsonQueryNode parent) {
        super(value, parent);
    }

    @Override
    public String toString() {
        if (this.value != null) {
            var builder = new StringBuilder();
            builder.append("[");
            for (var node:
                 this.nodes) {
                builder.append(node.toString());
            }
            builder.append("]");
            return builder.toString();
        } else {
            return "[]";
        }
    }

    @Override
    public JValue[] execute(JValue json, Consumer<JValue> action) {
        JValue[] passingValues = new JValue[]{};
        if (this.value != null) {
            if (this.value.token == JsonQueryParser.Token.NUMBER) {
                passingValues = new JValue[]{json.get(Integer.parseInt(this.value.value))};
            } else if (this.value.token == JsonQueryParser.Token.STRING) {
                passingValues = new JValue[]{json.get(this.value.stringValue())};
            }
        } else {
            passingValues = ((JArray)json).values();
        }
        if (this.nodes.size() > 0) {
            for (var value: passingValues) {
                this.child().execute(value, action);
            }
            return passingValues;
        } else {
            for (var value: passingValues) {
                action.accept(value);
            }
            return passingValues;
        }
    }
}
