package backend.exceptions;

public class GameNotStartedYetException extends Exception {
    public GameNotStartedYetException(String message) {
        super(message);
    }
}
