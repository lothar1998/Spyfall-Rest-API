package backend.endpoints.responses.location;

import backend.endpoints.responses.Response;

import java.util.Map;

/**
 * implementation of response for unique code query
 *
 * @author Piotr Kuglin
 */
public class UniqueCodeResponse extends Response {

    private final static String localResponseType = "unique_code";

    /**
     * create unique code response
     */
    public UniqueCodeResponse() {
        super(localResponseType,null,null);
    }

    /**
     * construct unique code response object with given status and content
     * @param status shows whether request was properly
     * @param content defines content of response
     */
    public UniqueCodeResponse(Status status, Map<String, Object> content) {
        super(localResponseType, status, content);
    }
}
