package backend.databases.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "players")
public class Players {

    @Id
    private ObjectId _id;
    private Accounts user;
    private Roles role;

    public Players() {
    }

    public Players(ObjectId _id, Accounts user, Roles role) {
        this._id = _id;
        this.user = user;
        this.role = role;
    }

    public String get_id() {
        return _id.toHexString();
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public Accounts getUser() {
        return user;
    }

    public void setUser(Accounts user) {
        this.user = user;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }
}
