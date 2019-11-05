package backend.exceptions.game;

public class GameHasAlreadyStartedException extends Exception {
    public GameHasAlreadyStartedException(String message) {
        super(message);
    }
}
