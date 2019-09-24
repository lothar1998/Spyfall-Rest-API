package backend.models.request.game;

import backend.databases.entities.LocationEntity;
import backend.databases.entities.RoleEntity;
import backend.databases.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

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
    private Date gameStart;

    @NotNull
    private LocationEntity location;

    @NotNull
    private List<RoleEntity> roles;
}
