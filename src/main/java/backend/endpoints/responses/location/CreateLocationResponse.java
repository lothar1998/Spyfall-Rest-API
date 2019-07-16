package backend.endpoints.responses.location;

import backend.endpoints.responses.Response;

import java.util.Map;

/**
 * implementation of response for create location query
 *
 * @author Piotr Kuglin
 */
public class CreateLocationResponse extends Response {

    private final static String localResponseType = "create_location";

    /**
     * create response for create location query
     */
    public CreateLocationResponse() {
        super(localResponseType,null,null);
    }

    /**
     * create response for create location query with status and content
     * @param status shows whether changing password was properly
     * @param content defines content of response
     */
    public CreateLocationResponse(Status status, Map<String, Object> content) {
        super(localResponseType, status, content);
    }
}
