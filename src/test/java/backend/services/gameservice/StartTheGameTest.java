package backend.services.gameservice;

import backend.config.ContextPaths;
import backend.config.ProfileTypes;
import backend.config.SpyCreation;
import backend.databases.entities.GameEntity;
import backend.databases.entities.LocationEntity;
import backend.databases.entities.RoleEntity;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.GameRepository;
import backend.databases.repositories.LocationRepository;
import backend.databases.repositories.RoleRepository;
import backend.databases.repositories.UserRepository;
import backend.exceptions.ExceptionDescriptions;
import backend.exceptions.ExceptionMessages;
import backend.models.response.ExceptionResponse;
import backend.models.response.Response;
import backend.parsers.JwtDecoder;
import backend.parsers.Parser;
import backend.parsers.UsernameParser;
import backend.services.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for starting the game routine
 * @author kamkalis
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GameService.class)
@ActiveProfiles(value = ProfileTypes.TEST_PROFILE)
public class StartTheGameTest {
    private final static String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJleHAiOjE1NjkyNjYxNjQsInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiJdLCJqdGkiOiIzYjlmYWVmMi0zMDVjLTRkY2UtOGNhMC0wYzEyMWYwYTA1ZGIiLCJjbGllbnRfaWQiOiJjbGllbnRfaWQiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXX0" +
            ".4-CyQK6tkXyH5YKvoAKSyrr1Pp5nWuG7mNLk8gFheLw";
    private final static String gameId = "507f1f77bcf86cd799439011";
    private final static String spyId = "ab7f1f88b8ff6cd799439044";

    private static ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private GameRepository gameRepository;
    @MockBean
    private LocationRepository locationRepository;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private Parser<String> parser;

    private Parser<String> usernameParser = new UsernameParser(new JwtDecoder());


    @Mock
    private UserEntity userEntity;
    @Mock
    private RoleEntity roleEntity;
    @InjectMocks
    private LocationEntity locationEntity;

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void should_occur_not_found_exception_caused_by_game_not_correct() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.GAME_NOT_FOUND,
                ExceptionDescriptions.NOT_FOUND, HttpStatus.NOT_FOUND);

        Mockito.when(gameRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(put(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_START + "/" + gameId)
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_occur_database_exception_caused_by_user_not_correct() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.DATABASE_ERROR,
                ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(gameRepository.findById(Mockito.anyString())).thenReturn(Optional.of(new GameEntity()));

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(null);

        mockMvc.perform(put(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_START + "/" + gameId)
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + token))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_occur_game_already_started_exception() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.WARNING,
                ExceptionMessages.GAME_HAS_ALREADY_STARTED, ExceptionDescriptions.PERMISSION_DENIED, HttpStatus.FORBIDDEN);

        GameEntity gameEntity = new GameEntity(userEntity, Date.from(Instant.EPOCH), locationEntity,
                Collections.singletonMap("User", new RoleEntity()));
        gameEntity.setGameStarted(true);

        Mockito.when(gameRepository.findById(Mockito.anyString())).thenReturn(Optional.of(gameEntity));
        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(userEntity);

        mockMvc.perform(put(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_START + "/" + gameId)
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_validate_user_permissions() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.PERMISSION_VALIDATION_ERROR,
                ExceptionDescriptions.PERMISSION_DENIED, HttpStatus.FORBIDDEN);

        GameEntity game = new GameEntity(new UserEntity(), java.util.Date.from(Instant.EPOCH), locationEntity, Collections.emptyMap());

        Mockito.when(gameRepository.findById(Mockito.anyString())).thenReturn(Optional.of(game));

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(userEntity);

        mockMvc.perform(put(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_START + "/" + gameId)
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_occur_too_many_players_exception() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.GAME_LOCATION_NOT_ENOUGH_ROLES,
                ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.FORBIDDEN);

        LocationEntity location = new LocationEntity("Location", userEntity, "Desc",
                Collections.singletonList(new RoleEntity()), Date.from(Instant.EPOCH));
        Map<String, RoleEntity> map = new HashMap<>();
        map.put("User0", new RoleEntity());
        GameEntity game = new GameEntity(userEntity, java.util.Date.from(Instant.EPOCH), location, map);

        Mockito.when(gameRepository.findById(Mockito.anyString())).thenReturn(Optional.of(game));

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(userEntity);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(game);

        game.getPlayersWithRoles().put("User1", null);
        game.getPlayersWithRoles().put("User2", null);

        mockMvc.perform(put(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_START + "/" + gameId)
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_start_game_properly() throws Exception {
        LocationEntity location = new LocationEntity("Location", userEntity, "Desc",
                Collections.singletonList(new RoleEntity()), Date.from(Instant.EPOCH));
        Map<String, RoleEntity> map = new HashMap<>();
        map.put("User0", new RoleEntity());
        GameEntity game = new GameEntity(userEntity, java.util.Date.from(Instant.EPOCH), location, map);

        Mockito.when(gameRepository.findById(Mockito.anyString())).thenReturn(Optional.of(game));

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(userEntity);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(game);

        RoleEntity spy = new RoleEntity();
        spy.setOwner(userEntity);
        spy.setName(SpyCreation.SPY_NAME);
        spy.setDescription(SpyCreation.SPY_DESCRIPTION);
        spy.setId(spyId);

        Mockito.when(roleRepository.save(Mockito.any())).thenAnswer((Answer<RoleEntity>) invocation -> {
            Object[] args = invocation.getArguments();
            return (RoleEntity) args[0];
        });

        Mockito.when(gameRepository.save(Mockito.any())).thenAnswer((Answer<GameEntity>) invocation -> {
            Object[] args = invocation.getArguments();
            return (GameEntity) args[0];
        });

        mockMvc.perform(put(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_START + "/" + gameId)
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + token))
                .andExpect(status().isOk());
    }
}
