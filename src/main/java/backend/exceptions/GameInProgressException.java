package backend.exceptions;

public class GameInProgressException extends Exception {
    public GameInProgressException(String message) {
        super(message);
    }
}
