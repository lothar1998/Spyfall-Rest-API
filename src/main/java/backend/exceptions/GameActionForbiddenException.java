package backend.exceptions;

public class GameActionForbiddenException extends Exception {
    public GameActionForbiddenException(String message) {
        super(message);
    }
}
