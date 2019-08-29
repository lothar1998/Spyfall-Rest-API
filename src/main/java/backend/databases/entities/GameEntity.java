package backend.databases.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "games")
@NoArgsConstructor
@Data
public class GameEntity {

    @Id
    private String id;
    private UserEntity hostId;
    private LocalDateTime gameStart;
    private LocationEntity location;
    private RoleEntity players;
}
