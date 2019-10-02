package backend.databases.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

/**
 * Game entity
 * @author Kamil Kaliś
 */

@Document(collection = "games")
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class GameEntity {

    @Id
    private String id;

    @Field(value = "host")
    @NotBlank
    @DBRef(lazy = true)
    private @NonNull UserEntity host;

    @Field(value = "gameTime")
    @NotNull
    private @NonNull Date gameStart;

    @Field(value = "gameLocation")
    @NotBlank
    @DBRef(lazy = true)
    private @NonNull LocationEntity location;

    @Field(value = "playerRole")
    @NotBlank
    @DBRef(lazy = true)
    private @NonNull Map<String,RoleEntity> playersWithRoles;
}
