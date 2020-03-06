package backend.models.response.location;

import backend.databases.entities.LocationEntity;
import backend.models.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * location by id response
 *
 * @author Piotr Kuglin
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationByIdResponseDto extends Response {

    private LocationEntity location;

    public LocationByIdResponseDto(MessageType type, String message, LocationEntity location) {
        super(type, message);
        this.location = location;
    }
}
