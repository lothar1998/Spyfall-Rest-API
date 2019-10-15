package backend.databases.entities;

import backend.config.startup.StartupConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Document(collection = "roles")
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class RoleEntity {

    @Id
    private String id;

    @NotNull
    @DBRef(lazy = true)
    @JsonIgnore
    private @NonNull UserEntity owner;

    @NotNull
    @NotBlank
    @Size(min = StartupConfig.ROLE_NAME_MIN_LENGTH)
    private @NonNull String name;

    @NotNull
    private @NonNull String description;
}
