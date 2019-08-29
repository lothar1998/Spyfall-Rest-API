package backend.databases.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Role entity
 * @author Kamil Kaliś
 */

@Document(collection = "roles")
@NoArgsConstructor
@Data
public class RoleEntity {

    @Id
    private String id;

    @Field(value = "roleName")
    @NotNull
    @NotBlank
    private String name;

    @Field(value = "description")
    @NotNull
    @NotBlank
    private String description;
}
