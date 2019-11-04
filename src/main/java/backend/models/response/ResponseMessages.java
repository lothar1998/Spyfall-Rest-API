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
    public static final String GAME_GET_BY_USER = "user games found";
    public static final String GAME_CREATED = "game has been created";
    public static final String GAME_DELETED = "game has been deleted";
    public static final String GAME_PLAYER_JOINED = "new player has joined the game";
    public static final String GAME_LOCATION_UPDATED = "game location has been changed";
    public static final String GAME_HAS_STARTED = "game has started";
    public static final String GAME_FINISHED = "game has finished";
    public static final String PLAYER_ROLE = "role for given player shown";

    private ResponseMessages() {
    }
}
