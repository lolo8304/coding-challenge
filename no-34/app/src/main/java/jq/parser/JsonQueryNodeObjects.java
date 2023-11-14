package jq.parser;

import json.JsonParserException;
import json.model.JArray;
import json.model.JMember;
import json.model.JObject;
import json.model.JValue;

import java.util.function.Consumer;

public class JsonQueryNodeObjects extends JsonQueryNode{
    public JsonQueryNodeObjects(JsonQueryParser.TokenValue value) {
        super(value);
    }

    public JsonQueryNodeObjects(JsonQueryParser.TokenValue value, JsonQueryNode parent) {
        super(value, parent);
    }

    @Override
    public String toString() {
        if (this.value != null) {
            var builder = new StringBuilder();
            builder.append("{");
            var second = false;
            for (var node:
                 this.nodes) {
                if (second) {
                    builder.append(", ");
                }
                builder.append(node.toString());
                second = true;
            }
            builder.append("}");
            return builder.toString();
        } else {
            return "{}";
        }
    }

    @Override
    public JValue[] execute(JValue json, Consumer<JValue> action) {
        var newValue = new JObject();
        if (this.nodes.size() > 0) {
            // nodes will contain names for object
            for (var node: this.nodes) {
                var slotKey = node.value.value;
                var slotValue = json.get(slotKey);
                try {
                    newValue.addMember(new JMember(slotKey, slotValue));
                } catch (JsonParserException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        action.accept(newValue);
        return new JValue[] {newValue};
    }
}
