package backend.endpoints.responses.game;

import backend.endpoints.responses.Response;

import java.util.Map;

/**
 * implementation of response for start game query
 *
 * @author Piotr Kuglin
 */
public class StartGameResponse extends Response {

    private final static String localResponseType = "start_game";

    /**
     * construct start game response object
     */
    public StartGameResponse() {
        super(localResponseType,null,null);
    }

    /**
     * construct start game response object with given status and content
     * @param status shows whether changing password was properly
     * @param content defines content of response
     */
    public StartGameResponse(Status status, Map<String, Object> content) {
        super(localResponseType, status, content);
    }
}
