package jq.parser;

import json.model.JValue;

import java.util.function.Consumer;

public class JsonQueryNodeCurrent extends JsonQueryNode{
    public JsonQueryNodeCurrent(JsonQueryParser.TokenValue value) {
        super(value);
    }

    public JsonQueryNodeCurrent(JsonQueryParser.TokenValue value, JsonQueryNode parent) {
        super(value, parent);
    }

    @Override
    public JValue[] execute(JValue json, Consumer<JValue> action) {
        if (this.nodes.size() == 1) {
            return this.child().execute(json, action);
        } else {
            action.accept(json);
            return new JValue[]{json};
        }
    }
}
