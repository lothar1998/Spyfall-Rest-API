package backend.exceptions;

/**
 * exception messages
 *
 * @author Piotr Kuglin
 */
public class ExceptionMessages {
    public static final String VALIDATION_ERROR = "validation exception";
    public static final String USER_ALREADY_EXISTS = "user already exists";
    public static final String DATABASE_ERROR = "database error";
    public static final String LOCATION_NOT_FOUND = "location has not been found";
    public static final String DELETION_VALIDATION_ERROR = "user has not permissions to delete this resource";

    public static final String GAME_NOT_FOUND = "game has not been found";
    public static final String PERMISSION_VALIDATION_ERROR = "user hasn't got permissions to access this resource";
    public static final String PLAYER_ALREADY_IN_GAME = "user is already in game";
    public static final String GAME_HAS_ALREADY_STARTED = "game has already started";
    public static final String GAME_NOT_STARTED_YET = "game has not started yet";
    public static final String GAME_IN_PROGRESS = "game is in progress and is not disabled";
    public static final String GAME_FINISHED = "game has finished";
    public static final String GAME_LOCATION_NOT_ENOUGH_ROLES = "game location hasn't got enough roles";

    private ExceptionMessages() {
    }
}
