package backend.models.response.game;

import backend.databases.entities.GameEntity;
import backend.models.response.Response;

import java.util.List;

public class GameListResponseDto extends Response {

    private List<GameEntity> existingGames;

    public GameListResponseDto(MessageType type, String message, List<GameEntity> existingGames) {
        super(type, message);
        this.existingGames = existingGames;
    }
}
