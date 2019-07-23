package backend.oauth2;

import backend.databases.entities.UserEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

class CustomUser extends User {
    private static final long serialVersionUID = 1L;
    CustomUser(UserEntity user) {
        super(user.getUsername(), user.getPassword(), Collections.singleton(new SimpleGrantedAuthority(user.getAuthority())));
    }
}