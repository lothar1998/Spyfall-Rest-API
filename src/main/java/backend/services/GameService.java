package backend.services;


import backend.config.ContextPaths;
import backend.config.SpyCreation;
import backend.config.oauth2.UsersRoles;
import backend.databases.entities.GameEntity;
import backend.databases.entities.LocationEntity;
import backend.databases.entities.RoleEntity;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.GameRepository;
import backend.databases.repositories.LocationRepository;
import backend.databases.repositories.RoleRepository;
import backend.databases.repositories.UserRepository;
import backend.exceptions.DatabaseException;
import backend.exceptions.ExceptionMessages;
import backend.exceptions.NotFoundException;
import backend.exceptions.PermissionDeniedException;
import backend.exceptions.game.*;
import backend.models.request.game.GameCreationDto;
import backend.models.response.Response;
import backend.models.response.ResponseMessages;
import backend.models.response.game.*;
import backend.parsers.Parser;
import org.springframework.beans.factory.annotation.Autowired;
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
    private RoleRepository roleRepository;
    private Parser<String> parser;

    @Autowired
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
     * @param game   game properties
     * @param error  validation errors
     * @param header JWT authorization bearer token
     * @return query response
     * @throws DatabaseException occurs when database returns incorrect responses
     * @throws NotFoundException occurs when location cannot been found
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

        Map<String, RoleEntity> playersWithRoles = new HashMap<>();
        //Load first player (host)
        playersWithRoles.put(host.getUsername(), null);

        GameEntity gameToSave = new GameEntity(host, new Date(), location, playersWithRoles);

        gameToSave.setDisabledJoin(false);
        gameToSave.setGameStarted(false);
        gameToSave.setGameDisabled(false);

        GameEntity savedGame = gameRepository.save(gameToSave);
        gameToSave.setId(savedGame.getId());

        if (!savedGame.equals(gameToSave))
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new GameCreationResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_CREATED, gameToSave));
    }

    /**
     * get all existing games
     *
     * @return list of existing games
     */
    @Secured(UsersRoles.ADMIN)
    @GetMapping(ContextPaths.GAME_GET_ALL)
    public ResponseEntity getAllExistingGames() {

        List<GameEntity> gameList = gameRepository.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(new GameListResponseDto(Response.MessageType.STATUS, ResponseMessages.LIST_OF_GAMES_SHOWN, gameList));
    }


    /**
     * get finished game by its Id in database
     *
     * @param id Id of a game
     * @return game details
     * @throws NotFoundException occur when game is not found in database
     * @throws GameInProgressException occur when game is in progress
     */
    @Secured({UsersRoles.ADMIN, UsersRoles.USER})
    @GetMapping(ContextPaths.GAME_ID)
    public ResponseEntity getGameLobby(@PathVariable String id) throws NotFoundException, GameInProgressException {

        GameEntity game = checkGameCorrectness(id);
        isGameInProgress(game);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameByIdResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_GET_BY_ID, game));
    }


    /**
     * get info about game for given player
     *
     * @param header JWT authorization bearer token
     * @param id     id of a game
     * @return specific info for player
     * @throws DatabaseException occur when player is not found in database
     * @throws NotFoundException occur when game is not found in database
     */
    @Secured({UsersRoles.USER, UsersRoles.ADMIN})
    @GetMapping(ContextPaths.GAME_START + ContextPaths.GAME_ID)
    public ResponseEntity getGameByPlayer(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String header,
                                          @PathVariable String id) throws DatabaseException, NotFoundException, GameFinishedException, GameNotStartedYetException {

        UserEntity user = checkUserCorrectness(header);
        GameEntity game = checkGameCorrectness(id);

        isGameNotStarted(game);
        isGameFinished(game);

        Map<String, RoleEntity> roles = game.getPlayersWithRoles();
        RoleEntity playerRole = roles.get(user.getUsername());
        LocationEntity location = game.getLocation();
        Date time = game.getGameTime();


        return ResponseEntity.status(HttpStatus.OK)
                .body(new PlayerGameInfoResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_PLAYER_INFO, playerRole, location, time));
    }


    /**
     * get user games
     *
     * @param header JWT authorization bearer token
     * @return game details
     * @throws DatabaseException occur when user is not found in database
     * @throws NotFoundException occur when game is not found in database
     */
    @Secured({UsersRoles.ADMIN, UsersRoles.USER})
    @GetMapping(ContextPaths.GAME_GET_ALL + ContextPaths.GAME_USER)
    public ResponseEntity getGameByHost(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String header) throws DatabaseException {

        UserEntity user = checkUserCorrectness(header);
        List<GameEntity> game = gameRepository.getGameEntityByHost(user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameListResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_GET_BY_USER, game));
    }


    /**
     * edit game with given id
     *
     * @param header      JWT authorization bearer token
     * @return response with edited game
     * @throws NotFoundException         occurs if there is no game with given id
     * @throws DatabaseException         occurs if saving or loading data from database are wrong
     * @throws PermissionDeniedException occurs if user has no permissions to edit this game
     */
    @Secured({UsersRoles.USER, UsersRoles.ADMIN})
    @PutMapping("/{gameId}" + ContextPaths.GAME_LOCATION + "/{locationId}")
    public ResponseEntity updateGameLocation(@PathVariable String gameId,
                                             @PathVariable String locationId,
                                             @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header)
            throws NotFoundException, DatabaseException, PermissionDeniedException {

        GameEntity game = checkGameCorrectness(gameId);
        UserEntity player = checkUserCorrectness(header);
        checkUserPermissions(player, game);
        LocationEntity newLocation = checkLocationCorrectness(locationId);
        game.setLocation(newLocation);
        gameRepository.save(game);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameEditionResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_LOCATION_UPDATED, game));
    }


    /**
     * add player to existing game of given id
     *
     * @param id     ID of game to update
     * @param header JWT authorization bearer token
     * @return response with edited game
     * @throws NotFoundException      occurs if there is no game with given id
     * @throws DatabaseException      occurs if saving or loading data from database are wrong
     * @throws AlreadyInGameException occurs if user has already joined the game
     */
    @Secured({UsersRoles.ADMIN, UsersRoles.USER})
    @PutMapping(ContextPaths.GAME_JOIN + ContextPaths.GAME_ID)
    public ResponseEntity addPlayerToGame(@PathVariable String id,
                                          @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header
    ) throws NotFoundException, DatabaseException, AlreadyInGameException, JoiningToGameDisabledException {

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
     *
     * @param id     id of a game to start
     * @param header authorization JWT header
     * @return response with started game
     * @throws NotFoundException occurs if there is no game with given id
     */
    @PutMapping(ContextPaths.GAME_START + ContextPaths.GAME_ID)
    @Secured({UsersRoles.ADMIN, UsersRoles.USER})
    public ResponseEntity startGame(@PathVariable String id,
                                    @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header) throws NotFoundException, DatabaseException, TooManyPlayersException, PermissionDeniedException, GameHasAlreadyStartedException {

        GameEntity game = checkGameCorrectness(id);
        UserEntity host = checkUserCorrectness(header);

        isGameStarted(game);

        checkUserPermissions(host, game);
        game.setDisabledJoin(true);
        gameRepository.save(game);

        checkNumberOfPlayersAndRoles(game);

        //create instance of Spy
        RoleEntity spy = new RoleEntity();
        spy.setOwner(host);
        spy.setName(SpyCreation.SPY_NAME);
        spy.setDescription(SpyCreation.SPY_DESCRIPTION);
        RoleEntity savedSpy = roleRepository.save(spy);
        spy.setId(savedSpy.getId());

        //get players, roles from location and Map with players and corresponding roles
        Map<String, RoleEntity> playersWithRoles = game.getPlayersWithRoles();
        List<RoleEntity> rolesInGameLocation = game.getLocation().getRoles();

        List<String> shuffledPlayerKeys = new ArrayList<>(game.getPlayersWithRoles().keySet());
        Collections.shuffle(shuffledPlayerKeys);

        Collections.shuffle(rolesInGameLocation);
        rolesInGameLocation.add(0, spy);

        //set random roles for shuffled players
        int index = 0;
        for (String newKey : shuffledPlayerKeys) {
            playersWithRoles.put(newKey, rolesInGameLocation.get(index++));
        }

        game.setGameStarted(true);
        game.setGameTime(new Date());

        gameRepository.save(game);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameHasStartedResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_HAS_STARTED));
    }


    /**
     * Endpoint to finish a game
     *
     * @param id     Id in a database of game
     * @param header authorization JWT header
     * @return info about success
     * @throws NotFoundException          occurs if there is no game with given id
     * @throws DatabaseException          occurs if saving or loading data from database are wrong
     * @throws PermissionDeniedException  occurs if player don't have permission to modify resource
     * @throws GameNotStartedYetException occurs if game is not started yet
     */
    @Secured({UsersRoles.USER, UsersRoles.ADMIN})
    @PutMapping(ContextPaths.GAME_FINISH + ContextPaths.GAME_ID)
    public ResponseEntity finishGame(@PathVariable String id, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header)
            throws NotFoundException, DatabaseException, PermissionDeniedException, GameNotStartedYetException {

        GameEntity game = checkGameCorrectness(id);
        UserEntity host = checkUserCorrectness(header);

        checkUserPermissions(host, game);
        isGameNotStarted(game);

        game.setGameDisabled(true);
        game.setGameStarted(false);

        gameRepository.save(game);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameFinishedResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_FINISHED));
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
    @Secured({UsersRoles.ADMIN, UsersRoles.USER})
    @DeleteMapping(ContextPaths.GAME_ID)
    public ResponseEntity deleteExistingGame(@PathVariable String id, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header)
            throws NotFoundException, DatabaseException, PermissionDeniedException, GameHasAlreadyStartedException {
        GameEntity game = checkGameCorrectness(id);
        UserEntity user = checkUserCorrectness(header);
        checkUserPermissions(user, game);

        if (game.isGameDisabled()) {
            RoleEntity spy = roleRepository.findByName("Spy");
            roleRepository.delete(spy);
        }

        isGameStarted(game);

        gameRepository.delete(game);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GameDeletionResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_DELETED));
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
     * check whether user has permissions to edit game (user should be host of game to edit its properties)
     *
     * @param user         checked user
     * @param game game location to check
     * @throws PermissionDeniedException occurs if user has no permissions to edit location
     */
    private void checkUserPermissions(UserEntity user, GameEntity game) throws PermissionDeniedException {
        if (!game.getHost().equals(user))
            throw new PermissionDeniedException(ExceptionMessages.PERMISSION_VALIDATION_ERROR);
    }


    /**
     * check whether user has already joined the game
     *
     * @param player user to check
     * @param game   game to check
     * @throws AlreadyInGameException occurs if player is already in game
     * @author Kamil Kalis
     */
    private void isPlayerAlreadyInGame(UserEntity player, GameEntity game) throws AlreadyInGameException {
        if (game.getPlayersWithRoles().containsKey(player.getUsername()))
            throw new AlreadyInGameException(ExceptionMessages.PLAYER_ALREADY_IN_GAME);
    }

    /**
     * check whether player can join the game before its start
     *
     * @param game Game to join
     * @throws JoiningToGameDisabledException occurs when player can't join the game
     */
    private void isJoiningToGameDisabled(GameEntity game) throws JoiningToGameDisabledException {
        if (game.isDisabledJoin())
            throw new JoiningToGameDisabledException(ExceptionMessages.GAME_HAS_ALREADY_STARTED);
    }

    /**
     * check if game has already started
     *
     * @param game game from DB to check
     * @throws GameHasAlreadyStartedException occurs when host already started the game
     */
    private void isGameStarted(GameEntity game) throws GameHasAlreadyStartedException {
        if (game.isGameStarted())
            throw new GameHasAlreadyStartedException(ExceptionMessages.GAME_HAS_ALREADY_STARTED);
    }

    /**
     * check if game doesn't yet started
     *
     * @param game game from DB to check
     * @throws GameNotStartedYetException occurs when game not started
     */
    private void isGameNotStarted(GameEntity game) throws GameNotStartedYetException {
        if (!game.isGameStarted())
            throw new GameNotStartedYetException(ExceptionMessages.GAME_NOT_STARTED_YET);
    }

    /**
     * check if game is in progress
     *
     * @param game game to check
     * @throws GameInProgressException occurs when game is in progress
     */
    private void isGameInProgress(GameEntity game) throws GameInProgressException {
        if (!game.isGameDisabled() && game.isGameStarted())
            throw new GameInProgressException(ExceptionMessages.GAME_IN_PROGRESS);
    }

    private void isGameFinished(GameEntity game) throws GameFinishedException {
        if (game.isGameDisabled() && !game.isGameStarted())
            throw new GameFinishedException(ExceptionMessages.GAME_FINISHED);
    }

    /**
     * checks for number of players in game and number of roles in game location (including spy role)
     *
     * @param game Game with nested location in it
     * @throws TooManyPlayersException occurs when there are not enough roles to distribute among players
     */
    private void checkNumberOfPlayersAndRoles(GameEntity game) throws TooManyPlayersException {
        if (game.getPlayersWithRoles().size() - 1 > game.getLocation().getRoles().size())
            throw new TooManyPlayersException(ExceptionMessages.GAME_LOCATION_NOT_ENOUGH_ROLES);
    }
}