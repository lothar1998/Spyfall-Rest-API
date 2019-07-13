package backend.endpoints.responses.client;

import backend.endpoints.responses.Response;

/**
 * implementation of response for logout query
 *
 * @author Piotr Kuglin
 */
public class LogoutResponse extends Response {

    {
        response_type = "logout";
    }

    /**
     * create logout response
     */
    public LogoutResponse() {}

    /**
     * create logout response with given status
     * @param status shows whether changing password was properly
     */
    public LogoutResponse(Status status) {
        this.status = status;
    }
}
