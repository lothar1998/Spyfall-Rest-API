package backend.endpoints.responses.client;

import backend.endpoints.responses.Response;

/**
 * implementation of response for query of login operation
 */
public class LoginResponse extends Response {

    {
        response_type = "login_response";
    }

    /**
     * create login response object
     */
    public LoginResponse() {}

    /**
     * create login response object with given status
     * @param status shows whether changing password was properly
     */
    public LoginResponse(Status status) {
        this.status = status;
    }
}
