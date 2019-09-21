package backend.models.response.location;

import backend.databases.entities.LocationEntity;
import backend.models.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationCreationResponseDto extends Response {

    private LocationEntity location;

    public LocationCreationResponseDto(MessageType type, String message, LocationEntity location) {
        super(type, message);
        this.location = location;
    }
}
