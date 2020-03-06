package backend.exceptions;

/**
 * exception descriptions
 *
 * @author Piotr Kuglin
 */
public class ExceptionDescriptions {
    public static final String BAD_REQUEST = "one or more properties are wrong";
    public static final String INTERNAL_SERVER_ERROR = "server encountered an error";
    public static final String PERMISSION_DENIED = "you have no permission to access this resource";
    public static final String NOT_FOUND = "resource has not been found";

    private ExceptionDescriptions() {
    }
}
