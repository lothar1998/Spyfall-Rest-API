package backend.config.startup;

import backend.config.ProfileTypes;
import backend.config.oauth2.UsersRoles;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * startup config for production profile
 *
 * @author Piotr Kuglin
 */
@EnableAspectJAutoProxy
@EnableAuthorizationServer
@EnableResourceServer
@Configuration
@Profile(ProfileTypes.PRODUCTION_PROFILE)
public class StartupConfig {

    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int USERNAME_MIN_LENGTH = 5;
    public static final String HASHED_PASSWORD_REPLACEMENT = "#HASHED#";
    private PasswordEncoder encoder;
    private UserRepository repository;
    @Value("${default.admin.username}")
    private String adminUserName;
    @Value("${default.admin.password}")
    private String adminPassword;
    @Value("${default.admin.email}")
    private String adminEmail;

    @Autowired
    public StartupConfig(PasswordEncoder encoder, UserRepository repository) {
        this.encoder = encoder;
        this.repository = repository;
    }

    /**
     * create admin user in DB if not exist
     */
    @Bean
    CommandLineRunner init() {
        return args -> {
            if (repository.findUserByUsername(adminUserName) == null)
                repository.save(new UserEntity(adminUserName, encoder.encode(adminPassword), adminEmail, UsersRoles.ADMIN));
        };
    }

    @Bean
    @ConditionalOnMissingBean(ResponseErrorHandler.class)
    public ResponseErrorHandler responseErrorHandler() {
        return new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }
        };
    }
}
