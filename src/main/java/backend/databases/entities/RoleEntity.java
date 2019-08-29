package backend.databases.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
@NoArgsConstructor
@Data
public class RoleEntity {

    @Id
    private String id;
    private String name;
    private String description;
}
