package backend.endpoints.responses;


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

    protected String response_type;
    protected Status status;

    /**
     * construct response object
     */
    public Response() {
    }

    /**
     * construct response object with given type and status
     * @param response_type type of response
     * @param status response whether query was successful
     */
    public Response(String response_type, Status status) {
        this.response_type = response_type;
        this.status = status;
    }

    /**
     * get type of response
     * @return type of response
     */
    public String getResponse_type() {
        return response_type;
    }

    /**
     * set type of response
     * @param response_type type of response
     */
    public void setResponse_type(String response_type) {
        this.response_type = response_type;
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
    public void setStatus(Status status) {
        this.status = status;
    }
}
