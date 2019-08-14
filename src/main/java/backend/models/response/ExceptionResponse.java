package backend.models.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * pattern of exception response handled by @link RestControllerExceptionHandler
 *
 * @author Piotr Kuglin
 */
@Getter
@Setter
public class ExceptionResponse extends Response {

    private String description;
    private String status;

    public ExceptionResponse(Response.MessageType type, String message, String description, HttpStatus status) {
        super(type, message);
        this.description = description;
        this.status = status.toString();
    }
}
