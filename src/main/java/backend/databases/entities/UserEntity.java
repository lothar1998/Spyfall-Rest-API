package backend.databases.entities;

import backend.config.startup.StartupConfig;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * user entity
 *
 * @author Piotr Kuglin
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "username")
    @NotNull
    @NotBlank
    @Size(min = StartupConfig.USERNAME_MIN_LENGTH)
    private String username;

    @Column(name = "password")
    @NotNull
    @NotBlank
    @Size(min = StartupConfig.PASSWORD_MIN_LENGTH)
    private String password;

    @Column(name = "email")
    @NotNull
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9-_.]+@[a-z0-9-.]+.[a-z0-9]{1,4}")
    private String email;

    @Column(name = "authority")
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
