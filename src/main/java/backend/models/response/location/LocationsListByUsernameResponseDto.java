package backend.models.response.location;

import backend.databases.entities.LocationEntity;
import backend.models.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * list all user's locations by username response
 *
 * @author Piotr Kuglin
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationsListByUsernameResponseDto extends Response {

    private List<LocationEntity> locations;

    public LocationsListByUsernameResponseDto(MessageType type, String message, List<LocationEntity> locations) {
        super(type, message);
        this.locations = locations;
    }
}
