package backend.exceptions;

/**
 * not found exception
 *
 * @author Piotr Kuglin
 */
public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}
