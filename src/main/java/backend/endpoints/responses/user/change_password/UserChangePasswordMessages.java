package backend.endpoints.responses.user.change_password;

/**
 * response messages for change password query
 *
 * @author Piotr Kuglin
 */
public class UserChangePasswordMessages {

    public static final String BAD_CREDENTIALS = "bad credentials";
    public static final String PASSWORD_HAS_BEEN_CHANGED = "password has been changed";
    public static final String DATABASE_ERROR = "database error";

    private UserChangePasswordMessages() {
    }
}
