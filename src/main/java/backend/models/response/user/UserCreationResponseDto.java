package backend.models.response.user;

import backend.databases.entities.UserEntity;
import backend.models.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * user creation response pattern
 *
 * @author Piotr Kuglin
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCreationResponseDto extends Response {

    private UserEntity user;
    private String message;

    public UserCreationResponseDto(MessageType type, String message, UserEntity user) {
        super(type);
        this.message = message;
        this.user = user;
    }
}
