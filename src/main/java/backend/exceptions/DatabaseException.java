package backend.exceptions;

/**
 * database error exception
 *
 * @author Piotr Kuglin
 */
public class DatabaseException extends Exception {
    public DatabaseException(String message) {
        super(message);
    }
}
