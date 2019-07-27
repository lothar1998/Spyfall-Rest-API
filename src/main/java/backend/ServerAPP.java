package backend;

import backend.oauth2.OAuth2Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableAuthorizationServer
@EnableResourceServer
@EnableConfigurationProperties({OAuth2Config.class, StartupConfig.class})
public class ServerAPP {
    public static void main(String... args){
        SpringApplication.run(ServerAPP.class);
    }



}
