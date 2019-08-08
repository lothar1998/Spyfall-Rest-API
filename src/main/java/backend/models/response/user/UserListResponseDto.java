package backend.models.response.user;

import backend.databases.entities.UserEntity;
import backend.models.response.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
public class UserListResponseDto extends Response {

    private List<UserEntity> signedUsers;

    public UserListResponseDto(MessageType type, List<UserEntity> users) {
        super(type);
        this.signedUsers = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserListResponseDto that = (UserListResponseDto) o;
        return Objects.equals(signedUsers, that.signedUsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), signedUsers);
    }
}
