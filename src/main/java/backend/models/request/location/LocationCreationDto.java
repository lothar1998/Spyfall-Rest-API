package backend.models.request.location;

import backend.config.startup.StartupConfig;
import backend.databases.entities.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * location creation request pattern
 *
 * @author Piotr Kuglin
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LocationCreationDto {

    @NotNull
    @NotBlank
    @Size(min = StartupConfig.LOCATION_NAME_MIN_LENGTH)
    private String name;

    @NotNull
    private String description;

    @NotNull
    private List<RoleEntity> roles;
}
