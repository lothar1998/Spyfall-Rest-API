package backend.databases.repositories;

import backend.databases.entities.LocationEntity;
import backend.databases.entities.RoleEntity;
import backend.databases.entities.UserEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LocationRepository extends MongoRepository<LocationEntity, String> {
    LocationEntity findByName(String name);
    LocationEntity findByOwner(UserEntity owner);
    LocationEntity findByDescription(String description);
    LocationEntity findByModificationDate(Date modificationDate);
    LocationEntity findByRoles(RoleEntity roles);
}