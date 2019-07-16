package backend.endpoints.responses.game;

import backend.endpoints.responses.Response;

import java.util.Map;

/**
 * implementation of response for synchronize query
 *
 * @author Piotr Kuglin
 */
public class SynchronizeResponse extends Response {

    private final static String localResponseType = "synchronize";

    /**
     * construct synchronize response object
     */
    public SynchronizeResponse() {
        super(localResponseType,null,null);
    }

    /**
     * create response for synchronize query with status and content
     * @param status shows whether changing password was properly
     * @param content defines content of response
     */
    public SynchronizeResponse(Status status, Map<String, Object> content) {
        super(localResponseType, status, content);
    }
}
