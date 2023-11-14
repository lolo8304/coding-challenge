package jq.parser;

import json.model.JValue;

import java.util.function.Consumer;

public class JsonQueryNodePipe extends JsonQueryNode{
    public JsonQueryNodePipe(JsonQueryParser.TokenValue value) {
        super(value);
    }

    public JsonQueryNodePipe(JsonQueryParser.TokenValue value, JsonQueryNode parent) {
        super(value, parent);
    }

    @Override
    public JValue[] execute(JValue json, Consumer<JValue> action) {
        if (this.nodes.size() > 0) {
            return this.child().execute(json, action);
        }
        return new JValue[] {json};
    }
}
