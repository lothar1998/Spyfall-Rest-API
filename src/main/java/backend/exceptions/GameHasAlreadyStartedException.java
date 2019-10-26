package backend.exceptions;

public class GameHasAlreadyStartedException extends Exception {
    public GameHasAlreadyStartedException(String message) {
        super(message);
    }
}
