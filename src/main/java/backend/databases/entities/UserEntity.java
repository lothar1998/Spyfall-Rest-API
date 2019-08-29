package backend.databases.entities;

import backend.config.startup.StartupConfig;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * user entity
 *
 * @author Piotr Kuglin
 */
@Document(collection = "users")
@NoArgsConstructor
@Data
public class UserEntity {

    @Id
    private String id;

    @Field(value = "username")
    @NotNull
    @NotBlank
    @Size(min = StartupConfig.USERNAME_MIN_LENGTH)
    private String username;

    @Field(value = "password")
    @NotNull
    @NotBlank
    @Size(min = StartupConfig.PASSWORD_MIN_LENGTH)
    private String password;

    @Field(value = "email")
    @NotNull
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9-_.]+@[a-z0-9-.]+.[a-z0-9]{1,4}")
    private String email;

    @Field(value = "authority")
    @NotNull
    @NotBlank
    private String authority;

    public UserEntity(@NotNull @NotBlank @Size(min = StartupConfig.USERNAME_MIN_LENGTH) String username,
                      @NotNull @NotBlank @Size(min = StartupConfig.PASSWORD_MIN_LENGTH) String password,
                      @NotNull @NotBlank @Pattern(regexp = "[a-zA-Z0-9-_.]+@[a-z0-9-.]+.[a-z0-9]{1,4}") String email,
                      @NotNull @NotBlank String authority) {

        this.username = username;
        this.password = password;
        this.email = email;
        this.authority = authority;
    }
}
