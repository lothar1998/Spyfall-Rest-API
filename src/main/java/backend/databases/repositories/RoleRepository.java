package backend.databases.repositories;

import backend.databases.entities.RoleEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends MongoRepository<RoleEntity, String> {
    List<RoleEntity> findById(ObjectId id);
}
