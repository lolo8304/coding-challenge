package json.model;

import json.JsonSerializeOptions;

import java.io.IOException;

public interface Serializer {
    public JsonBuilder serialize(JsonBuilder builder);
}
