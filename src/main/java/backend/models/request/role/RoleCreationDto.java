package backend.models.request.role;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Role creation request pattern
 *
 * @author Kamil Kaliś
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleCreationDto {

    @NotBlank
    private String roleName;

    @NotBlank
    private String roleDescription;
}
