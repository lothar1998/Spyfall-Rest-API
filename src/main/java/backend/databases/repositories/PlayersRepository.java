package backend.databases.repositories;

import backend.databases.entities.Players;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayersRepository extends MongoRepository<Players,String> {
    List<Players> findBy_id(ObjectId _id);
}
