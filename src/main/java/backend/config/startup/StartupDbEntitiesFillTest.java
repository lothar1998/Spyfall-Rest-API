package backend.config.startup;

import backend.databases.entities.GameEntity;
import backend.databases.entities.LocationEntity;
import backend.databases.entities.RoleEntity;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.GameRepository;
import backend.databases.repositories.LocationRepository;
import backend.databases.repositories.RoleRepository;
import backend.databases.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

/**
 * MongoDB database structure test
 *
 * @author Kamil Kaliś
 */

@Component
public class StartupDbEntitiesFillTest implements CommandLineRunner {

    private RoleRepository roleRepository;
    private LocationRepository locationRepository;
    private UserRepository userRepository;
    private GameRepository gameRepository;

    public StartupDbEntitiesFillTest(RoleRepository roleRepository, LocationRepository locationRepository, UserRepository userRepository, GameRepository gameRepository) {
        this.roleRepository = roleRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        //drop everything...
        this.roleRepository.deleteAll();
        this.locationRepository.deleteAll();
        this.gameRepository.deleteAll();


        UserEntity userOne = new UserEntity(
                "Username",
                "password12345",
                "mail@mail.com",
                "user"
        );

        this.userRepository.save(userOne);

        RoleEntity roleOne = new RoleEntity(
                "Init Role One",
                "Init Role One Description"
        );
        RoleEntity roleTwo = new RoleEntity(
                "Init Role Two",
                "Init Role Two Description"
        );


        this.roleRepository.save(roleOne);
        this.roleRepository.save(roleTwo);

        LocationEntity locationOne = new LocationEntity(
                "location name",
                userOne,
                "location Description",
                new Date(),
                Arrays.asList(roleOne,roleTwo)
        );

        this.locationRepository.save(locationOne);

        GameEntity gameOne = new GameEntity(
                userOne,
                new Date(),
                locationOne,
                roleOne
        );

        this.gameRepository.save(gameOne);
    }
}
