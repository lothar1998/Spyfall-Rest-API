package backend.databases.entities;

import backend.config.startup.StartupConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * role entity
 *
 * @author Piotr Kuglin
 */
@Document(collection = "roles")
@NoArgsConstructor
@Data
@JsonIgnoreProperties(value = { "target" })
public class RoleEntity {

    @Id
    private String id;

    @NotNull
    @DBRef(lazy = true)
    @JsonIgnore
    private UserEntity owner;

    @NotNull
    @NotBlank
    @Size(min = StartupConfig.ROLE_NAME_MIN_LENGTH)
    private String name;

    @NotNull
    private String description;
}
