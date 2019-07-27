package backend.endpoints.responses.location;

import backend.endpoints.responses.Response;

import java.util.Map;

/**
 * implementation of response for edit location query
 *
 * @author Piotr Kuglin
 */
public class EditLocationResponse extends Response {

    private final static String localResponseType = "edit_location";

    /**
     * create response for edit location query
     */
    public EditLocationResponse() {
        super(localResponseType,null,null);
    }

    /**
     * construct edit location object with given status and content
     * @param status shows whether request was properly
     * @param content defines content of response
     */
    public EditLocationResponse(Status status, Map<String, Object> content) {
        super(localResponseType, status, content);
    }
}
