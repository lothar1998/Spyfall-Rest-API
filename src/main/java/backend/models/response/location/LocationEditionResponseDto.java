package backend.models.response.location;

import backend.databases.entities.LocationEntity;
import backend.models.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * create location response
 *
 * @author Piotr Kuglin
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationEditionResponseDto extends Response {

    private LocationEntity location;

    public LocationEditionResponseDto(MessageType type, String message, LocationEntity location) {
        super(type, message);
        this.location = location;
    }
}
