package backend.databases.repositories;

import backend.databases.entities.GameEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends MongoRepository<GameEntity, String> {
    List<GameEntity> findById(ObjectId id);
}
