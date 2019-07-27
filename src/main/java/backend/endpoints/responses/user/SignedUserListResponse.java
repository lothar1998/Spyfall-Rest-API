package backend.endpoints.responses.user;

import backend.endpoints.responses.Response;

import java.util.Map;

/**
 * implementation of response for list of signed users query
 *
 * @author Piotr Kuglin
 */
public class SignedUserListResponse extends Response {

    private final static String localResponseType = "list_of_users";

    /**
     * create list of signed users response
     */
    public SignedUserListResponse() {
        super(localResponseType, null, null);
    }

    /**
     * create list of signed users response with given status
     * @param status shows whether request was properly
     * @param content defines content of response
     */
    public SignedUserListResponse(Status status, Map<String, Object> content) {
        super(localResponseType, status, content);
    }
}
