package backend.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * abstract class implementing response for query
 *
 * @author Piotr Kuglin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Response {

    protected MessageType type;
    protected String message;

    /**
     * type of message
     */
    public enum MessageType {
        INFO, ERROR, WARNING, STATUS
    }

}
