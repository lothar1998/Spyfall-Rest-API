package backend.services.gameservice;

import backend.config.ContextPaths;
import backend.config.ProfileTypes;
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
import backend.models.response.ResponseMessages;
import backend.models.response.game.PlayerGameInfoResponseDto;
import backend.parsers.JwtDecoder;
import backend.parsers.Parser;
import backend.parsers.UsernameParser;
import backend.services.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
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

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



/**
 * Tests for checking to show particular game for given user
 * @author kamkalis
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GameService.class)
@ActiveProfiles(value = ProfileTypes.TEST_PROFILE)
public class ShowGameForPlayerTest {
    private final static String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJleHAiOjE1NjkyNjYxNjQsInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiJdLCJqdGkiOiIzYjlmYWVmMi0zMDVjLTRkY2UtOGNhMC0wYzEyMWYwYTA1ZGIiLCJjbGllbnRfaWQiOiJjbGllbnRfaWQiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXX0" +
            ".4-CyQK6tkXyH5YKvoAKSyrr1Pp5nWuG7mNLk8gFheLw";
    private final static String gameId = "507f1f77bcf86cd799439011";

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
    @InjectMocks
    private RoleEntity roleEntity;
    @InjectMocks
    private LocationEntity locationEntity;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_occur_database_exception_when_user_not_found() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.DATABASE_ERROR,
                ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(null);

        mockMvc.perform(get(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_START + "/" + gameId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_occur_not_found_exception_when_game_not_found() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.GAME_NOT_FOUND,
                ExceptionDescriptions.NOT_FOUND, HttpStatus.NOT_FOUND);

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(new UserEntity());
        Mockito.when(gameRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_START + "/" + gameId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_occur_game_action_forbidden_when_game_is_not_started() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.GAME_NOT_STARTED_YET,
                ExceptionDescriptions.PERMISSION_DENIED, HttpStatus.FORBIDDEN);

        GameEntity gameEntity = new GameEntity(userEntity, Date.from(Instant.EPOCH), locationEntity,
                Collections.emptyMap());
        gameEntity.setGameDisabled(false);
        gameEntity.setGameStarted(false);

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(new UserEntity());
        Mockito.when(gameRepository.findById(Mockito.anyString())).thenReturn(Optional.of(gameEntity));

        mockMvc.perform(get(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_START + "/" + gameId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_occur_game_action_forbidden_when_game_is_finished() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.GAME_FINISHED,
                ExceptionDescriptions.PERMISSION_DENIED, HttpStatus.FORBIDDEN);

        GameEntity gameEntity = new GameEntity(userEntity, Date.from(Instant.EPOCH), locationEntity,
                Collections.emptyMap());
        gameEntity.setGameStarted(false);
        gameEntity.setGameDisabled(true);

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(new UserEntity());
        Mockito.when(gameRepository.findById(Mockito.anyString())).thenReturn(Optional.of(gameEntity));

        mockMvc.perform(get(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_START + "/" + gameId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_show_game_info_to_user() throws Exception {
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        GameEntity game = new GameEntity(userEntity, Date.from(Instant.EPOCH), locationEntity,
                Collections.singletonMap("User", roleEntity));
        game.setGameStarted(true);
        game.setGameDisabled(false);

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(userEntity);
        Mockito.when(gameRepository.findById(Mockito.anyString())).thenReturn(Optional.of(game));
        Mockito.when(userEntity.getUsername()).thenReturn("User");

        PlayerGameInfoResponseDto responseDto = new PlayerGameInfoResponseDto(Response.MessageType.INFO,
                ResponseMessages.GAME_PLAYER_INFO, roleEntity, locationEntity, Date.from(Instant.EPOCH));

        mockMvc.perform(get(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_START + "/" + gameId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }
}
