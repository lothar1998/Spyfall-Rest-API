package backend.endpoints.responses.user.creation;

import backend.databases.entities.UserEntity;
import backend.endpoints.responses.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;


/**
 * user creation response pattern
 *
 * @author Piotr Kuglin
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCreationResponse extends Response {

    UserEntity user;
    private String message;

    public UserCreationResponse(MessageType type, String message, UserEntity user) {
        super(type);
        this.message = message;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserCreationResponse that = (UserCreationResponse) o;
        return Objects.equals(message, that.message) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), message, user);
    }
}
