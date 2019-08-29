package backend.databases.entities;

import lombok.*;
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
@RequiredArgsConstructor
@Data
public class RoleEntity {

    @Id
    private String id;

    @Field(value = "roleName")
    @NotNull
    @NotBlank
    private @NonNull String name;

    @Field(value = "description")
    @NotNull
    @NotBlank
    private @NonNull String description;
}
