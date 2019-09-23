package backend.models.response;

/**
 * response messages for queries
 *
 * @author Piotr Kuglin
 */
public class ResponseMessages {

    public static final String PASSWORD_HAS_BEEN_CHANGED = "password has been changed";
    public static final String USER_HAS_BEEN_CREATED = "user has been created";
    public static final String LIST_OF_USERS_SHOWN = "current list of users";

    public static final String LOCATION_HAS_BEEN_CREATED = "location has been created";
    public static final String LIST_OF_LOCATIONS_BY_USERNAME = "current list of all user locations";
    public static final String LOCATION_BY_ID = "location corresponding to an id";
    public static final String LOCATION_DELETION = "location has been successfully deleted";

    private ResponseMessages() {
    }
}
