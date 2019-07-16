package backend.endpoints.responses.client;

import backend.endpoints.responses.Response;

import java.util.Map;

/**
 * implementation of response for query of creating new client (user) operation
 *
 * @author Piotr Kuglin
 */
public class CreateClientResponse extends Response {

    private final static String localResponseType = "create_client";

    /**
     * create response for create client query
     */
    public CreateClientResponse() {
        super(localResponseType,null,null);
    }

    /**
     * create response for create client query with given status
     * @param status shows whether changing password was properly
     * @param content defines content of response
     */
    public CreateClientResponse(Status status, Map<String, Object> content) {
        super(localResponseType,status,content);
    }


}
