package backend.models.response.game;

import backend.databases.entities.GameEntity;
import backend.models.response.Response;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameCreationResponseDto extends Response {

    private GameEntity game;

    public GameCreationResponseDto(MessageType type, String message, GameEntity game) {
        super(type, message);
        this.game = game;
    }
}
