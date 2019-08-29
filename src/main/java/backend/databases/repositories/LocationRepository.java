package backend.databases.repositories;

import backend.databases.entities.LocationEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends MongoRepository<LocationEntity, String> {
    List<LocationEntity> findById(ObjectId id);
}
