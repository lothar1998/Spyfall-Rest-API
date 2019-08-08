package backend.exceptions;

/**
 * user has not been created
 *
 * @author Piotr Kuglin
 */
public class UserHasNotBeenCreatedException extends RuntimeException {
    public UserHasNotBeenCreatedException(String message) {
        super(message);
    }
}
