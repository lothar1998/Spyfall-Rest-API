package backend.models.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * pattern of exception response handled by @link RestControllerExceptionHandler
 *
 * @author Piotr Kuglin
 */
@Data
public class ExceptionResponse {

    private String type;
    private String message;
    private String description;
    private String status;

    public ExceptionResponse(ExceptionType type, String message, String description, HttpStatus status) {
        this.type = type.toString();
        this.message = message;
        this.description = description;
        this.status = status.toString();
    }

    public enum ExceptionType {
        ERROR, FATAL_ERROR, WARNING
    }
}
