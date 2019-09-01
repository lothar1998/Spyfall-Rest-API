package backend.models.response.role;


import backend.databases.entities.RoleEntity;
import backend.models.response.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * list of roles response pattern
 *
 * @author Kamil Kaliś
 */
@NoArgsConstructor
@Getter
@Setter
public class RoleListResponseDto extends Response {

    private List<RoleEntity> existingRoles;

    public RoleListResponseDto(MessageType type, String message, List<RoleEntity> roles){
        super(type, message);
        this.existingRoles = roles;
    }

}
