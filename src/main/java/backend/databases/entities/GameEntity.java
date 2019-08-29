package backend.databases.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
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
@Data
public class GameEntity {

    @Id
    private String id;

    @Field(value = "host")
    @NotNull
    @NotBlank
    @DBRef(lazy = true)
    private UserEntity hostId;

    @Field(value = "gameTime")
    @NotNull
    private GregorianCalendar gameStart;

    @Field(value = "gameLocation")
    @NotNull
    @DBRef(lazy = true)
    private LocationEntity location;

    @Field(value = "palyerRole")
    @NotNull
    @DBRef(lazy = true)
    private RoleEntity roles;
}
