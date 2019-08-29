package backend.databases.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "locations")
public class Locations {

    @Id
    private ObjectId _id;
    private String locationName;
    private List<Roles> roles;

    public Locations() {
    }

    public Locations(ObjectId _id, String locationName, List<Roles> roles) {
        this._id = _id;
        this.locationName = locationName;
        this.roles = roles;
    }

    public String get_id() {
        return _id.toHexString();
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public List<Roles> getRoles() {
        return roles;
    }

    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }
}
