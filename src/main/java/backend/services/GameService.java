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
import backend.exceptions.PermissionDeniedException;
import backend.models.request.game.GameCreationDto;
import backend.models.response.Response;
import backend.models.response.ResponseMessages;
import backend.models.response.game.*;
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

//TODO: implement getRandomRole function, also consider ALWAYS setting spy and how to do it


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
    public ResponseEntity createGame(@Valid @RequestBody GameCreationDto game,
                                     Errors error,
                                     @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header) throws Exception {
        if (error.hasErrors())
            throw new ValidationException(ExceptionMessages.VALIDATION_ERROR);

        UserEntity host = checkUserCorrectness(header);

        if (host == null)
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        LocationEntity location = checkLocationCorrectness(game.getLocation().getId());

        Map<String,RoleEntity> playersWithRoles = new HashMap<>();
        //Load first player (host)
        playersWithRoles.put(host.getUsername(),null);

        GameEntity gameToSave = new GameEntity(host, new Date(), location, playersWithRoles);
        GameEntity savedGame = gameRepository.save(gameToSave);
        gameToSave.setId(savedGame.getId());

        if (!savedGame.equals(gameToSave))
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new GameCreationResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_CREATED,gameToSave));
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


    @GetMapping(ContextPaths.GAME_ID)
    public ResponseEntity getGameById(@PathVariable String id) throws NotFoundException {

        GameEntity game = checkGameCorrectness(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameByIdResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_GET_BY_ID, game));
    }


    @Secured({UsersRoles.ADMIN,UsersRoles.USER})
    @PutMapping(ContextPaths.GAME_ID)
    public ResponseEntity updateGameLocation(@Valid @RequestBody GameCreationDto updatedGame,
                                             @PathVariable String id,
                                             Errors errors,
                                             @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header)
            throws NotFoundException, DatabaseException, PermissionDeniedException {

        if (errors.hasErrors())
            throw new ValidationException(ExceptionMessages.VALIDATION_ERROR);

        GameEntity game = checkGameCorrectness(id);
        UserEntity host = checkUserCorrectness(header);
        checkUserPermissions(host, game);

        //change location
        LocationEntity newLocation = checkLocationCorrectness(updatedGame.getLocation().getId());
        game.setLocation(newLocation);
        gameRepository.save(game);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameEditionResponseDto(Response.MessageType.INFO,ResponseMessages.GAME_UPDATED, game));
    }


    @Secured({UsersRoles.ADMIN,UsersRoles.USER})
    @DeleteMapping(ContextPaths.GAME_ID)
    public ResponseEntity deleteExistingGame(@PathVariable String id, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header)
            throws NotFoundException, DatabaseException, PermissionDeniedException {
        GameEntity game = checkGameCorrectness(id);
        UserEntity user = checkUserCorrectness(header);
        checkUserPermissions(user, game);

        gameRepository.delete(game);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameDeletionResponseDto(Response.MessageType.INFO,ResponseMessages.GAME_DELETED));
    }



    //TODO: not working and weird error message, need to debug it later
//    @GetMapping(ContextPaths.GAME_GET_BY_HOST)
//    public ResponseEntity getGameByHostName(@RequestParam String id) throws DatabaseException {
//
//        UserEntity host = checkUserCorrectness(id);
//        GameEntity game = gameRepository.findOneByHostId(host.getId());
//
//        if (game == null)
//            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(new GameByHostNameDto(Response.MessageType.INFO,ResponseMessages.GAME_GET_BY_HOST_NAME,game));
//    }


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


    /**
     * check whether user has permissions to location (user should be owner of location to edit it)
     *
     * @param user     checked user
     * @param location location to check
     * @throws PermissionDeniedException occurs if user has no permissions to edit location
     * @author Piotr Kuglin
     */
    private void checkUserPermissions(UserEntity user, GameEntity location) throws PermissionDeniedException {
        if (!location.getHost().equals(user))
            throw new PermissionDeniedException(ExceptionMessages.DELETION_VALIDATION_ERROR);
    }
}
