package backend.databases.repositories;

import backend.databases.entities.GameEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface GameRepository extends MongoRepository<GameEntity, String> {
    GameEntity findOneByHostId(String id);
}
