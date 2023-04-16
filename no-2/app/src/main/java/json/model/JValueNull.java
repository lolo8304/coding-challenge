package json.model;

public class JValueNull extends JValue {

    public JValueNull() {
    }

    @Override
    public Object value() {
        return null;
    }

    @Override
    public java.lang.String toString() {
        return "null";
    }
}
