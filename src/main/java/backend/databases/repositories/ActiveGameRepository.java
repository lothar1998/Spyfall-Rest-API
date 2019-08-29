package backend.databases.repositories;

import backend.databases.entities.ActiveGame;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActiveGameRepository extends MongoRepository<ActiveGame,String> {
    List<ActiveGame> findBy_id(ObjectId _id);
}
