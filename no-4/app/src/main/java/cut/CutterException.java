package cut;

public class CutterException extends Exception {

    public CutterException(String string) {
        super(string);
    }
    
    public CutterException(String message, Throwable cause) {
        super(message, cause);
    }
}