package backend.services;


import backend.config.ContextPaths;
import backend.config.oauth2.UsersRoles;
import backend.databases.entities.GameEntity;
import backend.databases.entities.LocationEntity;
import backend.databases.entities.RoleEntity;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.GameRepository;
import backend.databases.repositories.LocationRepository;
import backend.databases.repositories.RoleRepository;
import backend.databases.repositories.UserRepository;
import backend.exceptions.*;
import backend.models.request.game.GameCreationDto;
import backend.models.response.Response;
import backend.models.response.ResponseMessages;
import backend.models.response.game.*;
import backend.parsers.Parser;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotBlank;
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
    private RoleRepository roleRepository;
    private Parser<String> parser;

    public GameService(GameRepository gameRepository, UserRepository userRepository, LocationRepository locationRepository, RoleRepository roleRepository, Parser<String> parser) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.roleRepository = roleRepository;
        this.parser = parser;
    }


    /**
     * create game with given request
     *
     * @param game game properties
     * @param error   validation errors
     * @param header   JWT authorization bearer token
     * @return query response
     * @throws DatabaseException occurs when database returns incorrect responses
     */
    @Secured({UsersRoles.ADMIN, UsersRoles.USER})
    @PostMapping(ContextPaths.GAME_CREATE)
    public ResponseEntity createGame(@Valid @RequestBody GameCreationDto game,
                                     Errors error,
                                     @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header) throws DatabaseException, NotFoundException {
        if (error.hasErrors())
            throw new ValidationException(ExceptionMessages.VALIDATION_ERROR);

        UserEntity host = checkUserCorrectness(header);

        LocationEntity location = checkLocationCorrectness(game.getLocation().getId());

        Map<String,RoleEntity> playersWithRoles = new HashMap<>();
        //Load first player (host)
        playersWithRoles.put(host.getUsername(),null);

        GameEntity gameToSave = new GameEntity(host, new Date(), location, playersWithRoles);
        gameToSave.setDisabledJoin(false);
        gameToSave.setGameStart(false);
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
     */
    @Secured({UsersRoles.ADMIN,UsersRoles.USER})
    @GetMapping(ContextPaths.GAME_GET_ALL)
    public ResponseEntity getAllExistingGames() {

        List<GameEntity> gameList = gameRepository.findAll();

        List<GameEntity> games = new ArrayList<>(gameList);
        return ResponseEntity.status(HttpStatus.OK).body(new GameListResponseDto(Response.MessageType.STATUS, ResponseMessages.LIST_OF_GAMES_SHOWN, games));
    }


    /**
     * get game by its Id in database
     *
     * @param id Id of a game
     * @return game details
     * @throws NotFoundException occur when game is not found in database
     */
    @Secured({UsersRoles.ADMIN,UsersRoles.USER})
    @GetMapping(ContextPaths.GAME_ID)
    public ResponseEntity getGameById(@PathVariable String id) throws NotFoundException {

        GameEntity game = checkGameCorrectness(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameByIdResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_GET_BY_ID, game));
    }


    /**
     * edit game with given id
     *
     * @param updatedGame    new game game properties
     * @param id             ID of game to update
     * @param header         JWT authorization bearer token
     * @param errors         validation errors
     * @return response with edited game
     * @throws NotFoundException         occurs if there is no game with given id
     * @throws DatabaseException         occurs if saving or loading data from database are wrong
     * @throws PermissionDeniedException occurs if user has no permissions to edit this game
     */
    @Secured({UsersRoles.USER,UsersRoles.ADMIN})
    @PutMapping(ContextPaths.GAME_LOCATION + ContextPaths.GAME_ID)
    public ResponseEntity updateGameLocation(@Valid @RequestBody GameCreationDto updatedGame,
                                             @PathVariable String id,
                                             @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header,
                                             Errors errors) throws NotFoundException, DatabaseException, PermissionDeniedException {

        if (errors.hasErrors())
            throw new ValidationException(ExceptionMessages.VALIDATION_ERROR);

        GameEntity game = checkGameCorrectness(id);
        UserEntity player = checkUserCorrectness(header);
        checkUserPermissions(player, game);
        LocationEntity newLocation = checkLocationCorrectness(updatedGame.getLocation().getId());
        game.setLocation(newLocation);
        gameRepository.save(game);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameEditionResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_LOCATION_UPDATED, game));
    }


    /**
     * add player to existing game of given id
     *
     * @param id             ID of game to update
     * @param header         JWT authorization bearer token
     * @return response with edited game
     * @throws NotFoundException         occurs if there is no game with given id
     * @throws DatabaseException         occurs if saving or loading data from database are wrong
     * @throws AlreadyInGameException    occurs if user has already joined the game
     */
    @Secured({UsersRoles.ADMIN,UsersRoles.USER})
    @PutMapping(ContextPaths.GAME_JOIN + ContextPaths.GAME_ID)
    public ResponseEntity addPlayerToGame(@PathVariable String id,
                                          @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header
                                          ) throws NotFoundException, DatabaseException, AlreadyInGameException, JoiningToGameDisabled {

        GameEntity game = checkGameCorrectness(id);
        UserEntity player = checkUserCorrectness(header);


        isJoiningToGameDisabled(game);
        isPlayerAlreadyInGame(player, game);

        game.getPlayersWithRoles().put(player.getUsername(), null);
        gameRepository.save(game);


        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameEditionResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_PLAYER_JOINED, game));
    }

    /**
     * Endpoint to start a game
     * @param id        id of a game to start
     * @param header    authorization JWT header
     * @return          response with started game
     * @throws NotFoundException occurs if there is no game with given id
     */
    @PutMapping(ContextPaths.GAME_START + ContextPaths.GAME_ID)
    @Secured({UsersRoles.ADMIN, UsersRoles.USER})
    public ResponseEntity startGame(@PathVariable String id,
                                    @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header) throws NotFoundException, DatabaseException, TooManyPlayersException, PermissionDeniedException, GameHasAlreadyStarted {

        GameEntity game = checkGameCorrectness(id);
        UserEntity host = checkUserCorrectness(header);

        isGameStarted(game);

        checkUserPermissions(host,game);
        game.setDisabledJoin(true);
        checkNumberOfPlayersAndRoles(game);

        //create instance of Spy
        RoleEntity spy = new RoleEntity(host,"Spy",
                "You are the Spy! Try to figure out location of other players. Do not get disguised!");
        RoleEntity savedSpy = roleRepository.save(spy);
        spy.setId(savedSpy.getId());

        //get players, roles from location and Map with players and corresponding roles
        @NotBlank @NonNull Map<String, RoleEntity> playersWithRoles = game.getPlayersWithRoles();
        List<RoleEntity> rolesInGameLocation = game.getLocation().getRoles();
        rolesInGameLocation.add(spy);
        Collections.shuffle(rolesInGameLocation);

        //set random roles
        int index = 0;
        for (String key: playersWithRoles.keySet()){
            playersWithRoles.put(key,rolesInGameLocation.get(index));
            index++;
        }

        game.setGameStart(true);

        gameRepository.save(game);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameHasStartedResponseDto(Response.MessageType.INFO,ResponseMessages.GAME_HAS_STARTED,game));
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

        if(game.isGameStart())
        {
            RoleEntity spy = roleRepository.findByName("Spy");
            roleRepository.delete(spy);
        }

        gameRepository.delete(game);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameDeletionResponseDto(Response.MessageType.INFO,ResponseMessages.GAME_DELETED));
    }


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

    private void isJoiningToGameDisabled(GameEntity game) throws JoiningToGameDisabled{
        if(game.isDisabledJoin())
            throw new JoiningToGameDisabled(ExceptionMessages.GAME_HAS_ALREADY_STARTED);
    }

    private void isGameStarted(GameEntity game) throws GameHasAlreadyStarted{
        if(game.isGameStart())
            throw new GameHasAlreadyStarted(ExceptionMessages.GAME_HAS_ALREADY_STARTED);
    }

    private void checkNumberOfPlayersAndRoles(GameEntity game) throws TooManyPlayersException {
        if (game.getPlayersWithRoles().size()-1 > game.getLocation().getRoles().size())
            throw new TooManyPlayersException(ExceptionMessages.GAME_LOCATION_NOT_ENOUGH_ROLES);
    }
}