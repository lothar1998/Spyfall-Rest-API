package backend.endpoints;

/**
 * interface which storage context paths to controllers
 *
 * @author Piotr Kuglin
 */
public interface ContextPaths {

    /**
     * user controller paths
     * @see backend.endpoints.controllers.UserController
     */
    String USER_CREATE = "/user/create";
    String USER_LOGIN = "/user/login";
    String USER_LOGOUT = "/user/logout";
    String USER_CHANGE_PASSWORD = "/user/password";


    /**
     * game controller paths
     * @see backend.endpoints.controllers.GameController
     */
    String GAME_START = "/game/start";
    String GAME_STOP = "/game/stop";
    String GAME_NEXT_PLAYER = "/game/next";
    String GAME_SUMMARY = "/game/summary";
    String GAME_SYNCHRONIZE = "/game/synchronize";

    /**
     * location controller paths
     * @see backend.endpoints.controllers.LocationController
     */
    String LOCATION_CREATE = "/location/create";
    String LOCATION_EDIT = "/location/edit";
    String LOCATION_CODE = "/location/code";
    String LOCATION_ADD = "/location/add";


}
