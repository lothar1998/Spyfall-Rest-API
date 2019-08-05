package backend.endpoints.responses.user.creation;

/**
 * response messages for user creation query
 *
 * @author Piotr Kuglin
 */
public class UserCreationMessages {
    public static final String BAD_CREDENTIALS = "bad credentials";
    public static final String USER_ALREADY_EXISTS = "user already exists";
    public static final String DATABASE_ERROR = "database error";
    public static final String USER_HAS_BEEN_CREATED = "user has been created";

    private UserCreationMessages() {
    }
}
