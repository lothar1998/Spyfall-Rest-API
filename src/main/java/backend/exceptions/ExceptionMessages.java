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
    public static final String CHANGE_VALIDATION_ERROR = "user has not permissions modify this resource";
    public static final String PLAYER_ALREADY_IN_GAME = "user is already in game";

    private ExceptionMessages() {
    }
}
