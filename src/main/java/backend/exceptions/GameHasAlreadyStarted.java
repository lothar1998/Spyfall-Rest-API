package backend.exceptions;

public class GameHasAlreadyStarted extends Exception {
    public GameHasAlreadyStarted(String message) {
        super(message);
    }
}
