package backend.models.response.game;

import backend.databases.entities.GameEntity;
import backend.models.response.Response;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
public class GameHasStartedResponseDto extends Response {

    private GameEntity startedGame;

    public GameHasStartedResponseDto(MessageType type, String message, GameEntity game) {
        super(type, message);
        this.startedGame = game;
    }
}
