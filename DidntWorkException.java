@SuppressWarnings("serial")
public class DidntWorkException extends Exception {
    // a custom exception used during testing
    public DidntWorkException(String message) {
        super(message);
    }
}