package jq.parser;

import json.model.JArray;
import json.model.JValue;

import java.util.ArrayList;
import java.util.function.Consumer;

public class JsonQueryNodeArrayBuilder extends JsonQueryNode{
    public JsonQueryNodeArrayBuilder(JsonQueryParser.TokenValue value) {
        super(value);
    }

    public JsonQueryNodeArrayBuilder(JsonQueryParser.TokenValue value, JsonQueryNode parent) {
        super(value, parent);
    }

    @Override
    public JValue[] execute(JValue json, Consumer<JValue> action) {
        if (this.value != null) {
            var array = new JArray();
            for (int i = 0; i < this.nodes.size(); i++) {
                var node = this.nodes.get(i);
                node.execute(json, ( result -> {
                    array.addValue(result);
                }));
            }
            action.accept(array);
            return array.values();
        } else {
            throw new IllegalArgumentException("array builder must have a [ value");
        }
    }
}
