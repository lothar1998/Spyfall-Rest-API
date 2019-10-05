package backend.models.response.game;

import backend.databases.entities.GameEntity;
import backend.models.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameEditionResponseDto extends Response {

    GameEntity game;

    public GameEditionResponseDto(MessageType type, String message, GameEntity game) {
        super(type, message);
        this.game = game;
    }
}
