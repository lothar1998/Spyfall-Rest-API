package backend.endpoints.responses.game;

import backend.endpoints.responses.Response;

import java.util.Map;

/**
 * implementation of stop game response for appropriate query
 *
 * @author Piotr Kuglin
 */
public class StopGameResponse extends Response {

    private final static String localResponseType = "stop_game";

    /**
     * construct stop game response object
     */
    public StopGameResponse() {
        super(localResponseType,null,null);
    }

    /**
     * construct stop game response object with status and content
     * @param status shows whether changing password was properly
     * @param content efines content of response
     */
    public StopGameResponse(Status status, Map<String, Object> content) {
        super(localResponseType, status, content);
    }
}
