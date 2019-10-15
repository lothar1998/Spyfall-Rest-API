package backend.config;

import backend.services.LocationService;
import backend.services.UserService;


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
    public static final String USER_CHANGE_PASSWORD = "/password";
    public static final String USER_GET_ALL_USERS = "/all";

    /**
     * location controller paths
     * @see LocationService
     */
    public static final String LOCATION_MAIN_CONTEXT = "/location";
    public static final String LOCATION_CREATE = "/create";

    /**
     * Game Service context paths
     *
     */
    public static final String GAME_MAIN_CONTEXT = "/game";
    public static final String GAME_CREATE = "/create";
    public static final String GAME_GET_ALL = "/all";
    public static final String GAME_ID = "/{id}";
    public static final String GAME_LOCATION = "/location";
    public static final String GAME_JOIN = "/join";
    public static final String GAME_START = "/start";
    public static final String GAME_FIND = "/find";

    private ContextPaths() {
    }

}
