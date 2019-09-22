package backend.databases.repositories;

import backend.databases.entities.LocationEntity;
import backend.databases.entities.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LocationRepository extends MongoRepository<LocationEntity, String> {
    List<LocationEntity> findByOwner(UserEntity owner);

    List<LocationEntity> findByOwnerAndName(UserEntity owner, String name);
}