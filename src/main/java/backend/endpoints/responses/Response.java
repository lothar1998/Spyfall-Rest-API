package backend.endpoints.responses;

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

    /**
     * type of message
     */
    public enum MessageType {
        MESSAGE, ERROR, STATUS
    }

}
