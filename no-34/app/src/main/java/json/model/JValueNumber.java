package json.model;

public class JValueNumber extends JValue {

    public final Number number;

    public JValueNumber(Number number) {
        this.number = number;
    }

    @Override
    public Object value() {
        return number;
    }
}
