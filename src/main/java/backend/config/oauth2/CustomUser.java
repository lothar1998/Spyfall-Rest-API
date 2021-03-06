package backend.config.oauth2;

import backend.databases.entities.UserEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

/**
 * custom user
 *
 * @author Piotr Kuglin
 */
class CustomUser extends User {
    private static final long serialVersionUID = 1L;

    CustomUser(UserEntity user) {
        super(user.getUsername(), user.getPassword(), user.isEnabled(), true, true, true,
                Collections.singleton(new SimpleGrantedAuthority(user.getAuthority())));
    }
}