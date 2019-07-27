package backend.endpoints.responses.user;

import backend.endpoints.responses.Response;

import java.util.Map;

/**
 * implementation of response for query of changing password operation
 *
 * @author Piotr Kuglin
 */
public class ChangePasswordResponse extends Response {

    private final static String localResponseType = "change_password";

    /**
     * create response for "change password" query
     */
    public ChangePasswordResponse() {
        super(localResponseType, null, null);
    }

    /**
     * create response for "change password" query with status and content
     * @param status shows whether request was properly
     * @param content defines content of response
     */
    public ChangePasswordResponse(Status status, Map<String, Object> content) {
        super(localResponseType, status, content);
    }
}
