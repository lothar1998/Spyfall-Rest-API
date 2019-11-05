package backend.exceptions.game;

public class AlreadyInGameException extends Exception {
    public AlreadyInGameException(String message) {
        super(message);
    }
}
