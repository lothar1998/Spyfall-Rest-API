package backend.services;


import backend.config.ContextPaths;
import backend.config.oauth2.UsersRoles;
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
import backend.models.response.game.GameByHostNameDto;
import backend.models.response.game.GameByIdResponseDto;
import backend.models.response.game.GameCreationResponseDto;
import backend.models.response.game.GameListResponseDto;
import backend.parsers.Parser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.*;



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
    private Parser<String> parser;

    public GameService(GameRepository gameRepository, UserRepository userRepository, LocationRepository locationRepository, Parser<String> parser) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.parser = parser;
    }


    @Secured({UsersRoles.ADMIN, UsersRoles.ADMIN})
    @PostMapping(ContextPaths.GAME_CREATE)
    public ResponseEntity createGame(@Valid @RequestBody GameCreationDto game, Errors error, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header) throws Exception {
        if (error.hasErrors())
            throw new ValidationException(ExceptionMessages.VALIDATION_ERROR);

        UserEntity host = checkUserCorrectness(header);

        if (host == null)
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        LocationEntity location = checkLocationCorrectness(game.getLocation().getId());

        Map<String,RoleEntity> playersWithRoles = new HashMap<>();
        playersWithRoles.put(host.getUsername(),null);

        GameEntity gameToSave = new GameEntity(host, new Date(), location, playersWithRoles);
        GameEntity savedGame = gameRepository.save(gameToSave);
        gameToSave.setId(savedGame.getId());

        if (!savedGame.equals(gameToSave))
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new GameCreationResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_HAS_BEEN_CREATED,gameToSave));
    }


    @Secured(UsersRoles.ADMIN)
    @GetMapping(ContextPaths.GAME_GET_ALL)
    public ResponseEntity getAllExistingGames() throws DatabaseException {
        List<GameEntity> games = new ArrayList<>();

        Iterable<GameEntity> gameList = gameRepository.findAll();

        if (!gameList.iterator().hasNext())
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        gameList.forEach(games::add);
        return ResponseEntity.status(HttpStatus.OK).body(new GameListResponseDto(Response.MessageType.STATUS, ResponseMessages.LIST_OF_GAMES_SHOWN, games));
    }


    @GetMapping(ContextPaths.GAME_GET_BY_ID)
    public ResponseEntity getGameById(@PathVariable String id) throws NotFoundException {

        GameEntity game = checkGameCorrectness(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameByIdResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_GET_BY_ID,game));
    }


    //TODO: not working, need to debug it
    @GetMapping(ContextPaths.GAME_GET_BY_HOST)
    public ResponseEntity getGameByHostName(@RequestParam String id) throws DatabaseException {

        UserEntity host = checkUserCorrectness(id);
        GameEntity game = gameRepository.findOneByHostId(host.getId());

        if (game == null)
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameByHostNameDto(Response.MessageType.INFO,ResponseMessages.GAME_GET_BY_HOST_NAME,game));
    }

    //TODO: implement getRandomRole function, also consider ALWAYS setting spy and how to do it


    /**
     * check whether user exists in database
     *
     * @param header authorization JWT header
     * @return user entity from database
     * @throws DatabaseException occurs if user does not exist in database
     * @author Piotr Kuglin
     */
    private UserEntity checkUserCorrectness(String header) throws DatabaseException {
        UserEntity user = userRepository.findUserByUsername(parser.parse(header));

        if (user == null)
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        return user;
    }


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


    private GameEntity checkGameCorrectness(String id) throws NotFoundException {
        Optional<GameEntity> game = gameRepository.findById(id);

        if (!game.isPresent())
            throw new NotFoundException(ExceptionMessages.GAME_NOT_FOUND);

        return game.get();
    }
}
