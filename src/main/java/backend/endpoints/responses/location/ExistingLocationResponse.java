package backend.endpoints.responses.location;

import backend.endpoints.responses.Response;

import java.util.Map;

/**
 * implementation of response for existing location query
 *
 * @author Piotr Kuglin
 */
public class ExistingLocationResponse extends Response {

    private final static String localResponseType = "existing_location";

    /**
     * create response for existing location query
     */
    public ExistingLocationResponse() {
        super(localResponseType,null,null);
    }

    /**
     * construct existing location response object with given status and content
     * @param status shows whether changing password was properly
     * @param content defines content of response
     */
    public ExistingLocationResponse(Status status, Map<String, Object> content) {
        super(localResponseType, status, content);
    }
}
