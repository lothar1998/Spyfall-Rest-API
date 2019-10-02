package backend.models.response;

/**
 * response messages for queries
 *
 * @author Piotr Kuglin
 */
public class ResponseMessages {

    public static final String PASS_HAS_BEEN_CHANGED = "password has been changed";
    public static final String USER_HAS_BEEN_CREATED = "user has been created";
    public static final String LIST_OF_USERS_SHOWN = "current list of users";

    public static final String LOCATION_HAS_BEEN_CREATED = "location has been created";

    public static final String LIST_OF_GAMES_SHOWN = "list of existing games";
    public static final String GAME_GET_BY_ID = "game of corresponding id has been found";
//    public static final String GAME_GET_BY_HOST_NAME = "game found by host name";
    public static final String GAME_HAS_BEEN_CREATED = "game has been created";
    public static final String GAME_DELETED = "game has been deleted";

    private ResponseMessages() {
    }
}
