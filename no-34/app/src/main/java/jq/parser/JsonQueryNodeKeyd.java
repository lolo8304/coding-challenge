package jq.parser;

import json.model.JArray;
import json.model.JValue;

import java.util.function.Consumer;

public class JsonQueryNodeKeyd extends JsonQueryNode{
    public JsonQueryNodeKeyd(JsonQueryParser.TokenValue value) {
        super(value);
    }

    public JsonQueryNodeKeyd(JsonQueryParser.TokenValue value, JsonQueryNode parent) {
        super(value, parent);
    }

    @Override
    public JValue[] execute(JValue json, Consumer<JValue> action) {
        if (this.value != null) {
            var resultValue = json.get(this.value.value);
            if (this.nodes.size() > 0) {
                return this.child().execute(resultValue, action);
            } else {
                action.accept(resultValue);
            }
            return new JValue[] { resultValue };
        } else {
            throw new IllegalArgumentException("Keyd query node must have a value");
        }
    }
}
