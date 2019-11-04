package backend.models.response.game;

import backend.databases.entities.LocationEntity;
import backend.databases.entities.RoleEntity;
import backend.models.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerGameInfoResponseDto extends Response {
    private RoleEntity playerRole;
    private LocationEntity location;
    private Date date;

    public PlayerGameInfoResponseDto(MessageType type, String message, RoleEntity playerRole, LocationEntity location, Date date) {
        super(type, message);
        this.playerRole = playerRole;
        this.location = location;
        this.date = date;
    }
}
