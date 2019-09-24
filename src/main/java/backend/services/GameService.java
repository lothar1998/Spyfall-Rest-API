package backend.services;


import backend.config.ContextPaths;
import backend.databases.entities.GameEntity;
import backend.databases.entities.LocationEntity;
import backend.databases.entities.RoleEntity;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.GameRepository;
import backend.databases.repositories.UserRepository;
import backend.exceptions.DatabaseException;
import backend.exceptions.ExceptionMessages;
import backend.models.request.game.GameCreationDto;
import backend.models.response.Response;
import backend.models.response.ResponseMessages;
import backend.models.response.game.GameCreationResponseDto;
import backend.models.response.game.GameListResponseDto;
import backend.parsers.UserNameParser;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @PostMapping(ContextPaths.GAME_CREATE)
    public ResponseEntity createGame(@Valid @RequestBody GameCreationDto game, Errors error, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header) throws Exception {
        if (error.hasErrors())
            throw new ValidationException(ExceptionMessages.VALIDATION_ERROR);

        UserEntity host = userRepository.findUserByUsername(UserNameParser.getUsername(header));

        if (host == null)
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        //TODO: maybe Collection.addAll?
        List<RoleEntity> rolesToSave = new ArrayList<>();
        for (RoleEntity r : game.getLocation().getRoles()){
            rolesToSave.add(r);
        }

        GameEntity gameToSave = new GameEntity(host, new Date(), game.getLocation(), rolesToSave);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new GameCreationResponseDto(Response.MessageType.INFO, ResponseMessages.GAME_HAS_BEEN_CREATED,gameToSave));
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
