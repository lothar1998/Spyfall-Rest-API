package backend.endpoints.responses.user;

import backend.endpoints.responses.Response;

import java.util.Map;

/**
 * implementation of response for query of creating new user (user) operation
 *
 * @author Piotr Kuglin
 */
public class CreateUserResponse extends Response {

    private final static String localResponseType = "create_client";

    /**
     * create response for create user query
     */
    public CreateUserResponse() {
        super(localResponseType,null,null);
    }

    /**
     * create response for create user query with given status
     * @param status shows whether request was properly
     * @param content defines content of response
     */
    public CreateUserResponse(Status status, Map<String, Object> content) {
        super(localResponseType,status,content);
    }


}
