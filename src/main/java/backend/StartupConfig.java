package backend;

import backend.databases.entities.UserEntity;
import backend.databases.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@ConfigurationProperties("default")
public class StartupConfig {

    private PasswordEncoder encoder;
    private UserRepository repository;

    public final static int passwordMinLength = 8;
    public final static int usernameMinLength = 5;

    @Value("${default.admin.username}")
    private String adminUserName;
    @Value("${default.admin.password}")
    private String adminPassword;
    @Value("${default.admin.email}")
    private String adminEmail;

    @Autowired
    public StartupConfig(PasswordEncoder encoder,UserRepository repository) {
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
                repository.save(new UserEntity(adminUserName, encoder.encode(adminPassword), adminEmail, "ROLE_ADMIN"));

            repository.save(new UserEntity("janko123",encoder.encode("janko123"),"janko123","ROLE_USER"));//TEMPORARY
        };
    }
}
