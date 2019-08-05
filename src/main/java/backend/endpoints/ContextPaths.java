package backend.endpoints;

/**
 * class which stores context paths to controllers
 *
 * @author Piotr Kuglin
 */
public class ContextPaths {

    /**
     * user controller paths
     * @see backend.endpoints.controllers.UserController
     */
    public static final String USER_MAIN_CONTEXT = "/user";
    public static final String USER_CREATE = "/create";
    public static final String USER_LOGOUT = "/logout";
    public static final String USER_CHANGE_PASSWORD = "/password";
    public static final String USER_GET_ALL_USERS = "/all";

    private ContextPaths() {
    }

}
