package backend.models.response.game;

import backend.databases.entities.GameEntity;
import backend.models.response.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameByHostNameDto extends Response {

    private GameEntity game;

    public GameByHostNameDto(MessageType type, String message, GameEntity game) {
        super(type, message);
        this.game = game;
    }
}
