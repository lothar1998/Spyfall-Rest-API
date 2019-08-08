package backend.exceptions;

/**
 * database error exception
 *
 * @author Piotr Kuglin
 */
public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }
}
