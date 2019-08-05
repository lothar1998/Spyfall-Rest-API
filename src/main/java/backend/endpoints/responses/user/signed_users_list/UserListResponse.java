package backend.endpoints.responses.user.signed_users_list;

import backend.databases.entities.UserEntity;
import backend.endpoints.responses.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
public class UserListResponse extends Response {

    private List<UserEntity> signedUsers;

    public UserListResponse(MessageType type, List<UserEntity> users) {
        super(type);
        this.signedUsers = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserListResponse that = (UserListResponse) o;
        return Objects.equals(signedUsers, that.signedUsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), signedUsers);
    }
}
