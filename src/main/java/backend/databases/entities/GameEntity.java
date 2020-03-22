package backend.databases.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(value = { "target" })
public class GameEntity {

    @Id
    private String id;

    @Field(value = "host")
    @NotBlank
    @DBRef(lazy = true)
    private @NonNull UserEntity host;

    @Field(value = "gameTime")
    @NotNull
    private @NonNull Date gameTime;

    @Field(value = "gameLocation")
    @NotBlank
    @DBRef(lazy = true)
    private @NonNull LocationEntity location;

    @Field(value = "playerRole")
    @NotBlank
    @DBRef(lazy = true)
    private @NonNull Map<String,RoleEntity> playersWithRoles;

    @Field(value = "disabledJoin")
    @NotBlank
    @JsonIgnore
    private boolean disabledJoin;

    @Field(value = "gameStarted")
    @NotBlank
    @JsonIgnore
    private boolean gameStarted;


    @Field(value = "gameDisabled")
    @NotBlank
    @JsonIgnore
    private boolean gameDisabled;

}
