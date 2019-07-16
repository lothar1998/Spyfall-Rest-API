package backend.endpoints.responses.game;

import backend.endpoints.responses.Response;
import java.util.Map;

/**
 * implementation of response for next player query
 *
 * @author Piotr Kuglin
 */
public class NextPlayerResponse extends Response {

    private final static String localResponseType = "next_player";

    /**
     * construct next player response
     */
    public NextPlayerResponse() {
        super(localResponseType,null,null);
    }

    /**
     * construct next player response with given status and content
     * @param status shows whether changing password was properly
     * @param content defines content of response
     */
    public NextPlayerResponse(Status status, Map<String, Object> content) {
        super(localResponseType,status,content);
    }
}
