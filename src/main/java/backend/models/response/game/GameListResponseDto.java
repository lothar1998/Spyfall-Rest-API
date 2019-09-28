package backend.models.response.game;

import backend.databases.entities.GameEntity;
import backend.models.response.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class GameListResponseDto extends Response {

    private List<GameEntity> existingGames;

    public GameListResponseDto(MessageType type, String message, List<GameEntity> existingGames) {
        super(type, message);
        this.existingGames = existingGames;
    }
}
