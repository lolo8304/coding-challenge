package json.model;

import json.JsonSerializeOptions;

import java.io.IOException;

public abstract class JElement implements  Serializer {
    @Override
    public String toString() {
        return this.serialize(new JsonBuilder()).toString();
    }

}
