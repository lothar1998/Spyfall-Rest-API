package backend.models.response.user;

import backend.models.response.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * change password response pattern
 *
 * @author Piotr Kuglin
 */
@NoArgsConstructor
@Getter
@Setter
public class PasswordChangeResponseDto extends Response {

    private String message;

    public PasswordChangeResponseDto(MessageType type, String message) {
        super(type);
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PasswordChangeResponseDto response = (PasswordChangeResponseDto) o;
        return Objects.equals(message, response.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), message);
    }
}
