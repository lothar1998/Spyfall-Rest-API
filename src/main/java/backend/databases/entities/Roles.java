package backend.databases.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "roles")
public class Roles {

    @Id
    private ObjectId _id;
    private List<String> roles;

    public Roles() {
    }

    public Roles(ObjectId _id, List<String> roles) {
        this._id = _id;
        this.roles = roles;
    }

    public String get_id() {
        return _id.toHexString();
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}
