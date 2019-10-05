package backend.exceptions;

public class AlreadyInGameException extends Exception {
    public AlreadyInGameException(String message) {
        super(message);
    }
}
