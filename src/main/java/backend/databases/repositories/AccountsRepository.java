package backend.databases.repositories;

import backend.databases.entities.Accounts;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountsRepository extends MongoRepository<Accounts,String> {
    List<Accounts> findBy_id(ObjectId _id);
}
