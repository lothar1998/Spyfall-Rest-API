package backend.models.response.user;

import backend.databases.entities.UserEntity;
import backend.models.response.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


/**
 * list of users response pattern
 *
 * @author Piotr Kuglin
 */
@NoArgsConstructor
@Getter
@Setter
public class UserListResponseDto extends Response {

    private List<UserEntity> signedUsers;

    public UserListResponseDto(MessageType type, List<UserEntity> users) {
        super(type);
        this.signedUsers = users;
    }
}
