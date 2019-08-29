package backend.databases.repositories;

import backend.databases.entities.Locations;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationsRepository extends MongoRepository<Locations,String> {
    List<Locations> findBy_id(ObjectId _id);
}
