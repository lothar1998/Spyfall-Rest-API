package backend.models.response.game;

import backend.models.response.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameDeletionResponseDto extends Response {
    public GameDeletionResponseDto(MessageType type, String message) {
        super(type, message);
    }
}
