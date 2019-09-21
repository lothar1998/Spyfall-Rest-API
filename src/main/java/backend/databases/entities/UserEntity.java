package backend.databases.entities;

import backend.config.startup.StartupConfig;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * user entity
 *
 * @author Piotr Kuglin
 */
@Document(value = "users")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
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
    @Email
    private String email;

    @Field(value = "authority")
    @NotNull
    @NotBlank
    private String authority;

    @Field(value = "enabled")
    private boolean enabled;

    @Field(value = "signedDate")
    private Date signedDate;

    @Field(value = "lastLogged")
    private Date lastLogged;

    public UserEntity(@NotNull @NotBlank @Size(min = StartupConfig.USERNAME_MIN_LENGTH) String username,
                      @NotNull @NotBlank @Size(min = StartupConfig.PASSWORD_MIN_LENGTH) String password,
                      @NotNull @NotBlank @Email String email,
                      @NotNull @NotBlank String authority,
                      boolean enabled,
                      Date signedDate,
                      Date lastLogged) {

        this.username = username;
        this.password = password;
        this.email = email;
        this.authority = authority;
        this.enabled = enabled;
        this.signedDate = signedDate;
        this.lastLogged = lastLogged;
    }

    public String getId() {
        return id;
    }
}
