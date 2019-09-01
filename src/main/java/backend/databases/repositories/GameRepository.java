package backend.databases.repositories;

import backend.databases.entities.GameEntity;
import backend.databases.entities.LocationEntity;
import backend.databases.entities.RoleEntity;
import backend.databases.entities.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends MongoRepository<GameEntity, String> {
    GameEntity findByHost(UserEntity hostId);
    GameEntity findByGameStart(Date gameStart);
    GameEntity findByLocation(LocationEntity location);
    GameEntity findByRoles(RoleEntity roles);
}
