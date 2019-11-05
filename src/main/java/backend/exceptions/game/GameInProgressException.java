package backend.exceptions.game;

public class GameInProgressException extends Exception {
    public GameInProgressException(String message) {
        super(message);
    }
}
