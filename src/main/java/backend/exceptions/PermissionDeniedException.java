package backend.exceptions;

/**
 * permission denied exception
 *
 * @author Piotr Kuglin
 */
public class PermissionDeniedException extends Exception {
    public PermissionDeniedException(String message) {
        super(message);
    }
}
