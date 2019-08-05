package backend.endpoints.requests.user;

import backend.config.startup.StartupConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * user creation request pattern
 *
 * @author Piotr Kuglin
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCreationRequest {

    @NotNull
    @Size(min = StartupConfig.USERNAME_MIN_LENGTH)
    private String username;

    @NotNull
    @Size(min = StartupConfig.PASSWORD_MIN_LENGTH)
    private String password;

    @NotNull
    @Pattern(regexp = "[a-zA-Z0-9-_.]+@[a-z0-9-.]+.[a-z0-9]{1,4}")
    private String email;
}
