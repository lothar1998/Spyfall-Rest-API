package backend.endpoints.responses.game;

import backend.endpoints.responses.Response;

import java.util.Map;

/**
 * implementation of response for summary query
 *
 * @author Piotr Kuglin
 */
public class SummaryResponse extends Response {

    private final static String localResponseType = "summary";

    /**
     * construct summary response object
     */
    public SummaryResponse() {
        super(localResponseType,null,null);
    }

    /**
     * construct summary response object with given status and content
     * @param status shows whether changing password was properly
     * @param content defines content of response
     */
    public SummaryResponse(Status status, Map<String, Object> content) {
        super(localResponseType, status, content);
    }
}
