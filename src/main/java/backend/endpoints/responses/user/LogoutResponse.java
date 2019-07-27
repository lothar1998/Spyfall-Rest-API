package backend.endpoints.responses.user;

import backend.endpoints.responses.Response;

import java.util.Map;

/**
 * implementation of response for logout query
 *
 * @author Piotr Kuglin
 */
public class LogoutResponse extends Response {

    private final static String localResponseType = "logout";

    /**
     * create logout response
     */
    public LogoutResponse() {
        super(localResponseType,null,null);
    }

    /**
     * create logout response with given status
     * @param status shows whether request was properly
     * @param content defines content of response
     */
    public LogoutResponse(Status status, Map<String, Object> content) {
        super(localResponseType,status,content);
    }
}
