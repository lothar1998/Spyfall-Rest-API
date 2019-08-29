package backend.databases.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.GregorianCalendar;

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
    @NotNull
    @NotBlank
    @DBRef(lazy = true)
    private @NonNull UserEntity hostId;

    @Field(value = "gameTime")
    @NotNull
    private @NonNull GregorianCalendar gameStart;

    @Field(value = "gameLocation")
    @NotNull
    @DBRef(lazy = true)
    private @NonNull LocationEntity location;

    @Field(value = "palyerRole")
    @NotNull
    @DBRef(lazy = true)
    private @NonNull RoleEntity roles;
}
