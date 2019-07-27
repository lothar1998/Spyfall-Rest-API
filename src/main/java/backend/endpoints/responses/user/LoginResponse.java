package backend.endpoints.responses.user;

import backend.endpoints.responses.Response;

import java.util.Map;

/**
 * implementation of response for query of login operation
 *
 * @author Piotr Kuglin
 */
public class LoginResponse extends Response {

    private final static String localResponseType = "login_response";

    /**
     * create login response object
     */
    public LoginResponse() {
        super(localResponseType,null,null);
    }

    /**
     * create login response object with given status
     * @param status shows whether request was properly
     * @param content defines content of response
     */
    public LoginResponse(Status status, Map<String, Object> content) {
        super(localResponseType,status,content);
    }
}
