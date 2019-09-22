package backend.databases.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * location entity
 *
 * @author Piotr Kuglin
 */
@Document(collection = "locations")
@NoArgsConstructor
@Data
public class LocationEntity {

    @Id
    private String id;

    @Field(value = "name")
    @NotNull
    private String name;

    @Field(value = "owner")
    @NotNull
    @DBRef(lazy = true)
    @JsonIgnore
    private UserEntity owner;

    @Field(value = "description")
    private String description;

    @DBRef(lazy = true)
    private List<RoleEntity> roles;

    @Field(value = "lastModified")
    private Date date;

    public LocationEntity(@NotNull String name, @NotNull UserEntity owner, String description, List<RoleEntity> roles, Date date) {
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.roles = roles;
        this.date = date;
    }
}
