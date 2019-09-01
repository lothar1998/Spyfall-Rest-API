package backend.databases.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;

/**
 * Role entity
 * @author Kamil Kaliś
 */

@Document(collection = "roles")
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class RoleEntity {

    @Id
    private String id;

    @Field(value = "roleName")
    @NotBlank
    private @NonNull String name;

    @Field(value = "description")
    @NotBlank
    private @NonNull String description;
}
