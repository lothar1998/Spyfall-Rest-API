package backend.models.request.user;

import backend.config.startup.StartupConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * change user password query pattern
 *
 * @author Piotr Kuglin
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PasswordChangeDto {

    @NotNull
    @Size(min = StartupConfig.PASSWORD_MIN_LENGTH)
    private String oldPassword;
    @NotNull
    @Size(min = StartupConfig.PASSWORD_MIN_LENGTH)
    private String newPassword;
}
