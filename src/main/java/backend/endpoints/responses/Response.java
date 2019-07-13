package backend.endpoints.responses;

public class Response {

    private String response_type;

    public Response() {
    }

    public Response(String response_type) {
        this.response_type = response_type;
    }

    public String getResponse_type() {
        return response_type;
    }

    public void setResponse_type(String response_type) {
        this.response_type = response_type;
    }

    @Override
    public String toString() {
        return "Response{" +
                "response_type='" + response_type + '\'' +
                '}';
    }
}
