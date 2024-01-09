package json;

import json.model.JObject;
import json.model.JsonBuilder;

public class Result {
    private final JObject result;
    private final JsonParserException exception;

    public Result(JObject result) {
        this.result = result;
        this.exception = null;
    }
    public Result(JsonParserException exception) {
        this.result = null;
        this.exception = exception;
    }

    @Override
    public String toString() {
        if (this.result != null) {
            if (Json.verbose1()) {
                return result.serialize(new JsonBuilder(new JsonSerializeOptions(false))).toString();
            } else {
                return result.serialize(new JsonBuilder(new JsonSerializeOptions(true))).toString();
            }
        } else {
            return exception.getMessage();
        }
    }
}
