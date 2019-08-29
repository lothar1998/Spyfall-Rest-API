package backend.databases.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "accounts")
public class Accounts {

    @Id
    private ObjectId _id;
    private String username;
    private String password;
    private String email;
    private LocalDateTime creationDate;
    private LocalDateTime lastLogin;

    public Accounts(String username, String password, String email, LocalDateTime creationDate, LocalDateTime lastLogin) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.creationDate = creationDate;
        this.lastLogin = lastLogin;
    }
    public Accounts() {
    }

    public String get_id() {
        return _id.toHexString();
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}
