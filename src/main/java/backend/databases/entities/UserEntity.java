package backend.databases.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username") @NotNull @Size(min = 6)
    private String username;

    @Column(name = "password") @NotNull @Size(min = 8)
    private String password;

    @Column(name = "email") @NotNull
    private String email;

    @Column(name = "authority") @NotNull
    private String authority;

    public UserEntity() { }

    public UserEntity(String username, String password, String email, String authority) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.authority = authority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return username.equals(that.username) &&
                password.equals(that.password) &&
                email.equals(that.email) &&
                authority.equals(that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email, authority);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", authority='" + authority + '\'' +
                '}';
    }
}
