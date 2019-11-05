package backend.models.response.game;

import backend.models.response.Response;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
public class GameHasStartedResponseDto extends Response {

    public GameHasStartedResponseDto(MessageType type, String message) {
        super(type, message);
    }
}
