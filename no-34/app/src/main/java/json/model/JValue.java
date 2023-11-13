package json.model;

public abstract class JValue extends JElement {

    public static JValueString String(String string) {
        return new JValueString(string);
    }
    public static JValueNumber Number(Number number) {
        return new JValueNumber(number);
    }
    public static JValueBoolean True() {
        return new JValueBoolean(true);
    }
    public static JValueBoolean False() {
        return new JValueBoolean(false);
    }
    public static JValueNull Null() {
        return new JValueNull();
    }
    public abstract Object value();

    @Override
    public JsonBuilder serialize(JsonBuilder builder) {
        builder.append(this.value().toString());
        return builder;
    }
}
