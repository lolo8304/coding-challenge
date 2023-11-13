package json;
public class JsonParserException extends Exception {

    public JsonParserException(String message) {
        super(message);
    }
    public JsonParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
