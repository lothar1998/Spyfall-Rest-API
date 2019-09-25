package backend.models.request.game;

import backend.databases.entities.LocationEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;


/**
 * Game creation request pattern
 *
 * @author Kamil Kaliś
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GameCreationDto {

    @NotNull
    private LocationEntity location;
}
