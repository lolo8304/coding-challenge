package jq.parser;

import json.model.JValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class JsonQueryNode {
    protected final JsonQueryParser.TokenValue value;
    protected JsonQueryNode parent;
    protected final List<JsonQueryNode> nodes;

    public JsonQueryNode(JsonQueryParser.TokenValue value) {
        this.parent = null;
        this.nodes = new ArrayList<>();
        this.value = value;
    }
    public JsonQueryNode(JsonQueryParser.TokenValue value, JsonQueryNode parent) {
        this.parent = parent;
        this.nodes = new ArrayList<>();
        if (this.parent != null){
            this.parent.nodes.add(this);
        }
        this.value = value;
    }
    public JsonQueryNode setParent(JsonQueryNode parent) {
        if (this.parent != parent) {
            this.parent = parent;
            this.parent.nodes.add(this);
        }
        return this;
    }

    public JsonQueryNode root() {
        if (this.parent != null) {
            return this.parent.root();
        }
        return this;
    }

    public JsonQueryNode child() {
        if (this.nodes.size() == 1) {
            return this.nodes.get(0);
        }
        throw new IllegalArgumentException("0 or more than 1 child");
    }

    @Override
    public String toString() {
        if (this.nodes.size() > 0) {
            var builder = new StringBuilder();
            if (this.value != null) {
                builder.append(this.value.value);
            }
            for (var node : this.nodes) {
                builder.append(node.toString());
            }
            return builder.toString();
        } else {
            if (this.value != null) {
                return this.value.value;
            } else {
                return "";
            }
        }
    }

    public JValue[] execute(JValue json, Consumer<JValue> action) {
        return new JValue[]{};
    }
}
