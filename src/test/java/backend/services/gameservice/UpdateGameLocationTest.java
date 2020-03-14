package backend.services.gameservice;

import backend.config.ContextPaths;
import backend.config.ProfileTypes;
import backend.config.oauth2.UsersRoles;
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
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for game location update
 * @author kamkalis
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GameService.class)
@ActiveProfiles(value = ProfileTypes.TEST_PROFILE)
public class UpdateGameLocationTest {
    private final static String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJleHAiOjE1NjkyNjYxNjQsInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiJdLCJqdGkiOiIzYjlmYWVmMi0zMDVjLTRkY2UtOGNhMC0wYzEyMWYwYTA1ZGIiLCJjbGllbnRfaWQiOiJjbGllbnRfaWQiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXX0" +
            ".4-CyQK6tkXyH5YKvoAKSyrr1Pp5nWuG7mNLk8gFheLw";
    private final static String gameId = "507f1f77bcf86cd799439011";
    private final static String locationId = "ab7f1f88b8ff6cd799439044";

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

        mockMvc.perform(put(ContextPaths.GAME_MAIN_CONTEXT + "/" + gameId + ContextPaths.GAME_LOCATION + "/" + locationId)
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_occur_not_found_exception_caused_by_location_not_found() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.LOCATION_NOT_FOUND,
                ExceptionDescriptions.NOT_FOUND, HttpStatus.NOT_FOUND);

        GameEntity game = new GameEntity(userEntity, Date.from(Instant.EPOCH), locationEntity, Collections.emptyMap());

        Mockito.when(gameRepository.findById(Mockito.anyString())).thenReturn(Optional.of(game));

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(userEntity);

        Mockito.when(locationRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(put(ContextPaths.GAME_MAIN_CONTEXT + "/" + gameId + ContextPaths.GAME_LOCATION + "/" + locationId)
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

        mockMvc.perform(put(ContextPaths.GAME_MAIN_CONTEXT + "/" + gameId + ContextPaths.GAME_LOCATION + "/" + locationId)
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + token))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_occur_permission_denied_exception_due_to_invalid_user_permission() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.PERMISSION_VALIDATION_ERROR,
                ExceptionDescriptions.PERMISSION_DENIED, HttpStatus.FORBIDDEN);

        GameEntity game = new GameEntity(new UserEntity(), Date.from(Instant.EPOCH), locationEntity, Collections.emptyMap());

        Mockito.when(gameRepository.findById(Mockito.anyString())).thenReturn(Optional.of(game));

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(userEntity);

        mockMvc.perform(put(ContextPaths.GAME_MAIN_CONTEXT + "/" + gameId + ContextPaths.GAME_LOCATION + "/" + locationId)
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_set_new_location() throws Exception {
        UserEntity user = new UserEntity("username", "password1234", "email@email.com",
                "ADMIN", true, Date.from(Instant.EPOCH), Date.from(Instant.EPOCH));

        LocationEntity location = new LocationEntity("Location", user, "Description",
                Collections.singletonList(new RoleEntity()), Date.from(Instant.EPOCH));

        GameEntity game = new GameEntity(user, Date.from(Instant.EPOCH), locationEntity, Collections.emptyMap());
        Mockito.when(gameRepository.findById(Mockito.anyString())).thenReturn(Optional.of(game));

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(user);

        Mockito.when(locationRepository.findById(Mockito.anyString())).thenReturn(java.util.Optional.of(location));

        Mockito.when(gameRepository.save(Mockito.any())).thenAnswer((Answer<GameEntity>) invocation -> {
            Object[] args = invocation.getArguments();
            return (GameEntity) args[0];
        });

        mockMvc.perform(put(ContextPaths.GAME_MAIN_CONTEXT + "/" + gameId + ContextPaths.GAME_LOCATION + "/" + locationId)
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + token))
                .andExpect(status().isOk());
    }
}
