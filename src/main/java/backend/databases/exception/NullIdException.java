package backend.databases.exception;

public class NullIdException extends NullPointerException{
    public NullIdException(String username) {
        super(username + "'s ID is null!");
    }
}
