package backend.exceptions.game;

public class GameActionForbiddenException extends Exception {
    public GameActionForbiddenException(String message) {
        super(message);
    }
}
