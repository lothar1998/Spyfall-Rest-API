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
import backend.exceptions.*;
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


    /**
     * create location with given request
     *
     * @param game game properties
     * @param error   validation errors
     * @param header   JWT authorization bearer token
     * @return query response
     * @throws DatabaseException occurs when database returns incorrect responses
     */
    @Secured({UsersRoles.ADMIN, UsersRoles.ADMIN})
    @PostMapping(ContextPaths.GAME_CREATE)
    public ResponseEntity createGame(@Valid @RequestBody GameCreationDto game,
                                     Errors error,
                                     @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header) throws Exception {
        if (error.hasErrors())
            throw new ValidationException(ExceptionMessages.VALIDATION_ERROR);

        UserEntity host = checkUserCorrectness(header);

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


    /**
     * get all existing games
     *
     * @return list of existing games
     * @throws DatabaseException occur when owner is not found in database
     */
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


    /**
     * get game by its Id in database
     *
     * @param id Id of a game
     * @return game details
     * @throws NotFoundException occur when game is not found in database
     */
    @GetMapping(ContextPaths.GAME_ID)
    public ResponseEntity getGameById(@PathVariable String id) throws NotFoundException {

        GameEntity game = checkGameCorrectness(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameByIdResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_GET_BY_ID, game));
    }


    @Secured({UsersRoles.ADMIN,UsersRoles.USER})
    @PutMapping(ContextPaths.GAME_ID)
    public ResponseEntity updateGame(@Valid @RequestBody(required = false) GameCreationDto updatedGame,
                                             @PathVariable String id,
                                             @RequestParam(required = false, name = "putLocation", defaultValue = "false") boolean putLocation,
                                             @RequestParam(required = false, name = "putPlayer", defaultValue = "false") boolean putPlayer,
                                             Errors errors,
                                             @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header)
            throws NotFoundException, DatabaseException, PermissionDeniedException, AlreadyInGameException {

        if (errors.hasErrors())
            throw new ValidationException(ExceptionMessages.VALIDATION_ERROR);

        GameEntity game = checkGameCorrectness(id);
        UserEntity player = checkUserCorrectness(header);

        //change location
        if (putLocation) {
            checkUserPermissions(player, game);
            LocationEntity newLocation = checkLocationCorrectness(updatedGame.getLocation().getId());
            game.setLocation(newLocation);
            gameRepository.save(game);
        }

        //add Player
        if (putPlayer) {
            isPlayerAlreadyInGame(player, game);
            game.getPlayersWithRoles().put(player.getUsername(),null);
            gameRepository.save(game);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameEditionResponseDto(Response.MessageType.INFO,ResponseMessages.GAME_UPDATED, game));
    }


    /**
     * delete game by its ID
     *
     * @param id     ID of location
     * @param header authorization JWT header
     * @return info - successfully deleted game
     * @throws DatabaseException         occurs if there is no such user in database
     * @throws NotFoundException         occurs if game id is not valid
     * @throws PermissionDeniedException occurs if user has no permission to delete this resource
     */
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
//    public ResponseEntity getGameByHostName(@RequestParam(name = "host") String id) throws DatabaseException {
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


    /**
     * check whether game exists in database
     *
     * @param id ID of game
     * @return game entity from database
     * @throws NotFoundException occurs if game has not been found in database
     * @author Piotr Kuglin
     */
    private GameEntity checkGameCorrectness(String id) throws NotFoundException {
        Optional<GameEntity> game = gameRepository.findById(id);

        if (!game.isPresent())
            throw new NotFoundException(ExceptionMessages.GAME_NOT_FOUND);

        return game.get();
    }


    /**
     * check whether user has permissions to game (user should be host of game to edit its location)
     *
     * @param user     checked user
     * @param gameLocation game location to check
     * @throws PermissionDeniedException occurs if user has no permissions to edit location
     * @author Piotr Kuglin
     */
    private void checkUserPermissions(UserEntity user, GameEntity gameLocation) throws PermissionDeniedException {
        if (!gameLocation.getHost().equals(user))
            throw new PermissionDeniedException(ExceptionMessages.CHANGE_VALIDATION_ERROR);
    }


    /**
     * check whether user has already joined the game
     *
     * @param player    user to check
     * @param game      game to check
     * @throws AlreadyInGameException occurs if player is already in game
     * @author Kamil Kalis
     */
    private void isPlayerAlreadyInGame(UserEntity player, GameEntity game) throws AlreadyInGameException {
        if (game.getPlayersWithRoles().containsKey(player.getUsername()))
            throw new AlreadyInGameException(ExceptionMessages.PLAYER_ALREADY_IN_GAME);
    }
}