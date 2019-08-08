package backend.models.response.user;

import backend.models.response.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
