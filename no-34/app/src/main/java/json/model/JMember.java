package json.model;

public class JMember extends JElement {
    
    private final String key;
    private final JValue value;

    public JMember(String key, JValue value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() { return this.key; }
    public JValue getValue() { return this.value; }

    @Override
    public JsonBuilder serialize(JsonBuilder builder) {
        builder.append("\"").append(this.key).append("\": ");
        this.getValue().serialize(builder);
        return builder;
    }

}
