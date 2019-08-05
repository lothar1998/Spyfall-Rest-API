package backend.config.startup;

import backend.config.ProfileTypes;
import backend.config.oauth2.UsersRoles;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * startup config for development profile
 *
 * @author Piotr Kuglin
 */
@Configuration
@Profile(ProfileTypes.DEVELOPMENT_PROFILE)
public class StartupConfigDevelopment {

    private PasswordEncoder encoder;
    private UserRepository repository;

    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int USERNAME_MIN_LENGTH = 5;

    @Value("${default.admin.username}")
    private String adminUserName;
    @Value("${default.admin.password}")
    private String adminPassword;
    @Value("${default.admin.email}")
    private String adminEmail;

    @Autowired
    public StartupConfigDevelopment(PasswordEncoder encoder, UserRepository repository) {
        this.encoder=encoder;
        this.repository=repository;
    }

    /**
     * create admin user in DB if not exist
     */
    @Bean
    CommandLineRunner init(){
        return args -> {
            if(repository.findUserByUsername(adminUserName)==null)
                repository.save(new UserEntity(adminUserName, encoder.encode(adminPassword), adminEmail, UsersRoles.ADMIN));

            final String credentials = "janko123";
            repository.save(new UserEntity(credentials, encoder.encode(credentials), credentials, UsersRoles.USER));
        };
    }
}
