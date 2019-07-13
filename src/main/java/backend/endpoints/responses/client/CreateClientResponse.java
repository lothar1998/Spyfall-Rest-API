package backend.endpoints.responses.client;

import backend.endpoints.responses.Response;

/**
 * implementation of response for query of creating new client (user) operation
 *
 * @author Piotr Kuglin
 */
public class CreateClientResponse extends Response {

    {
        response_type = "create_client";
    }

    /**
     * create response for create client query
     */
    public CreateClientResponse() {}

    /**
     * create response for create client query with given status
     * @param status shows whether changing password was properly
     */
    public CreateClientResponse(Status status) {
        this.status = status;
    }


}
