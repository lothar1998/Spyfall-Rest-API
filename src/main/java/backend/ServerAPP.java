package backend;

import backend.databases.entities.UserEntity;
import backend.databases.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableAuthorizationServer
@EnableResourceServer
public class ServerAPP {
    public static void main(String... args){
        SpringApplication.run(ServerAPP.class);
    }

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    UserRepository repository;

    @Bean
    CommandLineRunner init(){
        return args -> {
          repository.save(new UserEntity("janeka",encoder.encode("testtest"),"test@tes.pl", "USER"));
        };
    }
}
