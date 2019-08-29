package backend.config.oauth2;

import backend.databases.entities.UserEntity;
import backend.databases.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Calendar;


/**
 * custom details service for user
 *
 * @author Piotr Kuglin
 */
@Service
public class CustomDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public CustomDetailsService(UserRepository userRepository){
        this.userRepository=userRepository;
    }


    @Override
    public CustomUser loadUserByUsername(String username) {

        UserEntity user = userRepository.findUserByUsername(username);

        if (user != null) {
            user.setLastLogged(Calendar.getInstance().getTime());
            userRepository.save(user);
            return new CustomUser(user);
        }
        else
            throw new UsernameNotFoundException(username + "not found!");

    }
}
