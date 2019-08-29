package backend.databases.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "active_game")
public class ActiveGame {

    @Id
    private ObjectId _id;
    private Accounts hostId;
    private LocalDateTime gameStart;
    private Locations location;
    private Players players;

    public ActiveGame() {
    }

    public ActiveGame(ObjectId _id, Accounts hostId, LocalDateTime gameStart, Locations location, Players players) {
        this._id = _id;
        this.hostId = hostId;
        this.gameStart = gameStart;
        this.location = location;
        this.players = players;
    }

    public String get_id() {
        return _id.toHexString();
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public Accounts getHostId() {
        return hostId;
    }

    public void setHostId(Accounts hostId) {
        this.hostId = hostId;
    }

    public LocalDateTime getGameStart() {
        return gameStart;
    }

    public void setGameStart(LocalDateTime gameStart) {
        this.gameStart = gameStart;
    }

    public Locations getLocation() {
        return location;
    }

    public void setLocation(Locations location) {
        this.location = location;
    }

    public Players getPlayers() {
        return players;
    }

    public void setPlayers(Players players) {
        this.players = players;
    }
}
