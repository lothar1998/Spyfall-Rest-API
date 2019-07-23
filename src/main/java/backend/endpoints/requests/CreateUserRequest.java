package backend.endpoints.requests;

import backend.databases.entities.UserEntity;

public class CreateUserRequest extends UserEntity {
    public CreateUserRequest() {
        super();
    }

    public CreateUserRequest(String username, String password, String email, String authority) {
        super(username, password, email, authority);
    }
}
