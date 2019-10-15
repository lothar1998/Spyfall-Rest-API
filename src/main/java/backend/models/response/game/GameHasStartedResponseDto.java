package backend.models.response.game;

import backend.databases.entities.GameEntity;
import backend.models.response.Response;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class GameHasStartedResponseDto extends Response {

    private GameEntity startedGame;

    public GameHasStartedResponseDto(MessageType type, String message, GameEntity game) {
        super(type, message);
        this.startedGame = game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameHasStartedResponseDto)) return false;
        if (!super.equals(o)) return false;
        GameHasStartedResponseDto that = (GameHasStartedResponseDto) o;
        return getStartedGame().equals(that.getStartedGame());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getStartedGame());
    }
}
