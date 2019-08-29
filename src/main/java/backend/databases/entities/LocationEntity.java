package backend.databases.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.GregorianCalendar;
import java.util.List;

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
    private UserEntity owner;

    @Field(value = "description")
    private String description;

    @Field(value = "lastModified")
    private GregorianCalendar date;

    @DBRef(lazy = true)
    private List<RoleEntity> roles;
}
