package backend.models.response.role;

import backend.databases.entities.RoleEntity;
import backend.models.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleCreationResponseDto extends Response {

    private RoleEntity role;

    public RoleCreationResponseDto(MessageType type, String message, RoleEntity role){
        super(type,message);
        this.role = role;
    }
}
