package backend.databases.entities;

import backend.config.startup.StartupConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@JsonIgnoreProperties(value = { "target" })
public class LocationEntity {

    @Id
    private String id;

    @Field(value = "name")
    @NotNull
    @NotBlank
    @Size(min = StartupConfig.LOCATION_NAME_MIN_LENGTH)
    private String name;

    @Field(value = "owner")
    @DBRef(lazy = true)
    @JsonIgnore
    private UserEntity owner;

    @Field(value = "description")
    @NotNull
    private String description;

    @DBRef(lazy = true)
    @NotNull
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
