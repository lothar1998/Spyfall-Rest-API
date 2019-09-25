package backend.services;


import backend.config.ContextPaths;
import backend.databases.entities.GameEntity;
import backend.databases.entities.LocationEntity;
import backend.databases.entities.RoleEntity;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.GameRepository;
import backend.databases.repositories.LocationRepository;
import backend.databases.repositories.UserRepository;
import backend.exceptions.DatabaseException;
import backend.exceptions.ExceptionMessages;
import backend.exceptions.NotFoundException;
import backend.models.request.game.GameCreationDto;
import backend.models.response.Response;
import backend.models.response.ResponseMessages;
import backend.models.response.game.GameCreationResponseDto;
import backend.models.response.game.GameListResponseDto;
import backend.parsers.UserNameParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.*;

import static java.util.Collections.singletonList;

//TODO: include game id as a paramether

/**
 * REST controller for Game queries
 *
 * @author Kamil Kaliś
 */
@RestController
@RequestMapping(ContextPaths.GAME_MAIN_CONTEXT)
public class GameService {

    private GameRepository gameRepository;
    private UserRepository userRepository;
    private LocationRepository locationRepository;

    public GameService(GameRepository gameRepository, UserRepository userRepository, LocationRepository locationRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    @PostMapping(ContextPaths.GAME_CREATE)
    public ResponseEntity createGame(@Valid @RequestBody GameCreationDto game, Errors error, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header) throws Exception {
        if (error.hasErrors())
            throw new ValidationException(ExceptionMessages.VALIDATION_ERROR);

        UserEntity host = userRepository.findUserByUsername(UserNameParser.getUsername(header));

        if (host == null)
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        LocationEntity location = checkLocationCorrectness(game.getLocation().getId());

        Map<RoleEntity,UserEntity> playersWithRoles = new HashMap<>();

        GameEntity gameToSave = new GameEntity(host, new Date(), location, playersWithRoles);

        GameEntity savedGame = gameRepository.save(gameToSave);

        gameToSave.setId(savedGame.getId());

        if (!savedGame.equals(gameToSave))
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new GameCreationResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_HAS_BEEN_CREATED,gameToSave));
    }

    //TODO: implement getRandomRole function, also consider ALWAYS setting spy and how to do it

    /**
     * check whether location exists in database
     *
     * @param id ID of location
     * @return location entity from database
     * @throws NotFoundException occurs if location has not been found in database
     * @author Piotr Kuglin
     */
    private LocationEntity checkLocationCorrectness(String id) throws NotFoundException {
        Optional<LocationEntity> locationOptional = locationRepository.findById(id);

        if (!locationOptional.isPresent())
            throw new NotFoundException(ExceptionMessages.LOCATION_NOT_FOUND);

        return locationOptional.get();
    }

    //TODO: something's wrong in here
    @GetMapping(ContextPaths.GAME_GET_ALL_GAMES)
    public ResponseEntity showAllGames() {
        List<GameEntity> games = new ArrayList<>();

        Iterable<GameEntity> gameList = gameRepository.findAll();

//        if (!gameList.iterator().hasNext())
//            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GameListResponseDto(Response.MessageType.STATUS, ResponseMessages.LIST_OF_GAMES_SHOWN,games));
    }
}
