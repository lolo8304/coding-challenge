package json.model;

public class JValueString extends JValue {

    public final String string;

    public JValueString(String string) {
        this.string = string;
    }

    @Override
    public Object value() {
        return "\""+string+"\"";
    }
}
