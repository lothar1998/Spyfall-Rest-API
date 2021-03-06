package backend.config.startup;

import backend.config.ProfileTypes;
import backend.config.oauth2.UsersRoles;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.UserRepository;
import backend.parsers.JwtDecoder;
import backend.parsers.Parser;
import backend.parsers.UsernameParser;
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
@Profile(ProfileTypes.TEST_PROFILE)
public class StartupConfigTest {

    private PasswordEncoder encoder;
    private UserRepository repository;

    @Value("${default.admin.username}")
    private String adminUserName;
    @Value("${default.admin.password}")
    private String adminPassword;
    @Value("${default.admin.email}")
    private String adminEmail;

    @Autowired
    public StartupConfigTest(PasswordEncoder encoder, UserRepository repository) {
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
                repository.save(new UserEntity(adminUserName, encoder.encode(adminPassword), adminEmail, UsersRoles.ADMIN, true, null, null));

            final String credentials = "janko123";
            repository.save(new UserEntity(credentials, encoder.encode(credentials), credentials, UsersRoles.USER, true, null, null));
        };
    }

    @Bean
    public Parser<String> usernameParser() {
        return new UsernameParser(new JwtDecoder());
    }
}
