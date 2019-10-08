package backend.databases.repositories;

import backend.databases.entities.GameEntity;
import backend.databases.entities.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface GameRepository extends MongoRepository<GameEntity, String> {
    GameEntity findByHost(UserEntity host);
}
