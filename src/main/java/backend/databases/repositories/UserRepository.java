package backend.databases.repositories;

import backend.databases.entities.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {
    UserEntity findUserByUsername(String username);
    UserEntity findUserByEmail(String email);
}
