package backend.exceptions.game;

public class TooManyPlayersException extends Exception {
    public TooManyPlayersException(String message) {
        super(message);
    }
}
