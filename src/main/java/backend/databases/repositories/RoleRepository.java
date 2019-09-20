package backend.databases.repositories;

import backend.databases.entities.RoleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<RoleEntity, String> {
    RoleEntity findByDescription(String description);
    RoleEntity findByName(String name);
}
