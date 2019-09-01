package backend.config;

import backend.services.UserService;
import backend.services.RoleService;
import backend.services.GameService;

/**
 * class which stores context paths to controllers
 *
 * @author Piotr Kuglin
 */
public class ContextPaths {

    /**
     * user controller paths
     * @see UserService
     */
    public static final String USER_MAIN_CONTEXT = "/user";
    public static final String USER_CREATE = "/create";
    public static final String USER_LOGOUT = "/logout";
    public static final String USER_CHANGE_PASSWORD = "/password";
    public static final String USER_GET_ALL_USERS = "/all";


    /**
     * Role Service context paths
     * @see RoleService
     */
    public static final String ROLE_MAIN_CONTEXT = "/roles";
    public static final String ROLE_CREATE = "/create";
    public static final String ROLE_GET_ALL_ROLES = "/all";


    /**
     * Game Service context paths
     * @see GameService
     */
    public static final String GAME_MAIN_CONTEXT = "/game";
    public static final String GAME_CREATE = "/create";

    private ContextPaths() {
    }

}
