package backend.endpoints.responses;

import java.util.Map;

/**
 * abstract class implementing response for query
 *
 * @author Piotr Kuglin
 */
public abstract class Response {

    /**
     * status of response
     */
    public enum Status {
        ok, error
    }

    private final String responseType;
    protected Status status;
    protected Map<String,Object> content;

    /**
     * construct response object
     */
    public Response() {
        responseType = null;
    }

    /**
     * construct response object with given type and status
     * @param responseType type of response
     * @param status response whether query was successful
     */
    public Response(String responseType, Status status, Map<String, Object> content) {
        this.responseType = responseType;
        this.status = status;
        this.content = content;
    }

    /**
     * get type of response
     * @return type of response
     */
    public String getresponseType() {
        return responseType;
    }

    /**
     * get status of response
     * @return status of response
     */
    public Status getStatus() {
        return status;
    }

    /**
     * set status of response
     * @param status response whether query was successful
     */
    public Response setStatus(Status status) {
        this.status = status;
        return this;
    }

    /**
     * get content of response
     * @return content
     */
    public Map<String, Object> getContent() {
        return content;
    }

    /**
     * set content of response
     * @param content specific content of response
     */
    public Response setContent(Map<String, Object> content) {
        this.content = content;
        return this;
    }
}
