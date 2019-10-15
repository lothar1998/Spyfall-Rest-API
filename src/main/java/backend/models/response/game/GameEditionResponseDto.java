package backend.models.response.game;

import backend.databases.entities.GameEntity;
import backend.models.response.Response;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameEditionResponseDto extends Response {
    private GameEntity game;

    public GameEditionResponseDto(MessageType type, String message, GameEntity game) {
        super(type, message);
        this.game = game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameEditionResponseDto)) return false;
        if (!super.equals(o)) return false;
        GameEditionResponseDto that = (GameEditionResponseDto) o;
        return getGame().equals(that.getGame());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getGame());
    }
}
