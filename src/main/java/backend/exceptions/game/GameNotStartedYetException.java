package backend.exceptions.game;

public class GameNotStartedYetException extends Exception {
    public GameNotStartedYetException(String message) {
        super(message);
    }
}
