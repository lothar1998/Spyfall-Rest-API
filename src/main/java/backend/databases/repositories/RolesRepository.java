package backend.databases.repositories;

import backend.databases.entities.Roles;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolesRepository extends MongoRepository<Roles,String> {
    List<Roles> findBy_id(ObjectId _id);
}
