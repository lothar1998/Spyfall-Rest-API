package backend.models.response.location;

import backend.models.response.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * location deletion response pattern
 *
 * @author Piotr Kuglin
 */
@NoArgsConstructor
@Getter
@Setter
public class LocationDeletionResponseDto extends Response {

    public LocationDeletionResponseDto(MessageType type, String message) {
        super(type, message);
    }
}
