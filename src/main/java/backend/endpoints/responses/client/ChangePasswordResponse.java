package backend.endpoints.responses.client;

import backend.endpoints.responses.Response;

/**
 * implementation of response for query of changing password operation
 *
 * @author Piotr Kuglin
 */
public class ChangePasswordResponse extends Response {

    {
        response_type = "change_password";
    }

    /**
     * create response for "change password" query
     */
    public ChangePasswordResponse() {}

    /**
     * create response for "change password" query with status
     * @param status shows whether changing password was properly
     */
    public ChangePasswordResponse(Status status) {
        this.status = status;
    }
}
