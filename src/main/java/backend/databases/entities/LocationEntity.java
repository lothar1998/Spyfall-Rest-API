package backend.databases.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

import java.util.Date;

import java.util.List;

@Document(collection = "locations")
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class LocationEntity {

    @Id
    private String id;

    @Field(value = "name")
    @NotNull
    private @NonNull String name;

    @Field(value = "owner")
    @NotNull
    @DBRef(lazy = true)
    private @NonNull UserEntity owner;

    @Field(value = "description")
    private @NonNull String description;

    @Field(value = "lastModified")
    private @NonNull Date modificationDate;

    @Field(value = "role")
    @NotNull
    @DBRef(lazy = true)
    private @NonNull List<RoleEntity> roles;
}
