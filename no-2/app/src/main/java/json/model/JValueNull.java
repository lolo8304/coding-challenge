package json.model;

public class JValueNull extends JValue {

    public JValueNull() {
    }

    @Override
    public Object value() {
        return null;
    }

    @Override
    public JsonBuilder serialize(JsonBuilder builder) {
        builder.append("null");
        return builder;
    }

    @Override
    public JValue get(int index) {
        return null;
    }

    @Override
    public JValue get(String key) {
        return null;
    }
}
