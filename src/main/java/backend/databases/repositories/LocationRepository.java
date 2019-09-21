package backend.databases.repositories;

import backend.databases.entities.LocationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LocationRepository extends MongoRepository<LocationEntity, String> {
}