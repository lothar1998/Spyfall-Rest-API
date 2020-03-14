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
import backend.models.request.game.GameCreationDto;
import backend.models.response.ExceptionResponse;
import backend.models.response.Response;
import backend.parsers.JwtDecoder;
import backend.parsers.Parser;
import backend.parsers.UsernameParser;
import backend.services.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.Before;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Tests for checking game creation
 * @author kamkalis
 */
@RunWith(SpringRunner.class)
@ActiveProfiles(value = ProfileTypes.TEST_PROFILE)
@WebMvcTest(value = GameService.class)
public class CreateGameTest {

    private final static String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJleHAiOjE1NjkyNjYxNjQsInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiJdLCJqdGkiOiIzYjlmYWVmMi0zMDVjLTRkY2UtOGNhMC0wYzEyMWYwYTA1ZGIiLCJjbGllbnRfaWQiOiJjbGllbnRfaWQiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXX0" +
            ".4-CyQK6tkXyH5YKvoAKSyrr1Pp5nWuG7mNLk8gFheLw";

    private static Gson gson = new Gson();
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

    @Mock
    private UserEntity user;
    @Mock
    private RoleEntity role;
    @InjectMocks
    private LocationEntity locationEntity;


    @Autowired
    private MockMvc mockMvc;
    private ExceptionResponse validationExceptionResponse;
    private ObjectMapper objectMapper = new ObjectMapper();
    private Parser<String> usernameParser = new UsernameParser(new JwtDecoder());


    @Before
    public void startingSetUp() {
        this.validationExceptionResponse = new ExceptionResponse(Response.MessageType.WARNING,
                ExceptionMessages.VALIDATION_ERROR, ExceptionDescriptions.BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void should_validate_location_is_not_null() throws Exception {
        GameCreationDto request = new GameCreationDto(null);

        mockMvc.perform(post(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_CREATE)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer" + token))
                .andExpect(status().isBadRequest()).andExpect(content().json(objectMapper.writeValueAsString(validationExceptionResponse)));
    }

    @Test
    public void should_validate_game_location_is_not_empty() throws Exception {
        GameCreationDto request = new GameCreationDto();

        mockMvc.perform(post(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_CREATE)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer" + token))
                .andExpect(status().isBadRequest()).andExpect(content().json(objectMapper.writeValueAsString(validationExceptionResponse)));
    }

    @Test
    public void should_occur_database_exception_when_game_host_not_found() throws Exception {
        GameCreationDto request = new GameCreationDto(locationEntity);

        ExceptionResponse response = new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.DATABASE_ERROR,
                ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(null);

        mockMvc.perform(post(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_CREATE)
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isInternalServerError()).andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_occur_database_error_caused_by_game_location_not_found() throws Exception {
        GameCreationDto request = new GameCreationDto(locationEntity);

        ExceptionResponse response = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.LOCATION_NOT_FOUND,
                ExceptionDescriptions.NOT_FOUND, HttpStatus.NOT_FOUND);

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(new UserEntity());
        Mockito.when(locationRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        mockMvc.perform(post(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_CREATE)
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound()).andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_occur_database_error_caused_by_game_saving_error() throws Exception {
        GameCreationDto request = new GameCreationDto(locationEntity);

        ExceptionResponse response = new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.DATABASE_ERROR, ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(null);

        mockMvc.perform(post(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_CREATE)
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isInternalServerError()).andExpect(content().json(objectMapper.writeValueAsString(response)));
    }


    @Test
    public void should_create_new_game() throws Exception {
        UserEntity user = new UserEntity("username", "password1234", "email@email.com",
                "ADMIN", true, Date.from(Instant.EPOCH), Date.from(Instant.EPOCH));

        LocationEntity location = new LocationEntity("Location", user, "Description",
                Collections.singletonList(new RoleEntity()), Date.from(Instant.EPOCH));

        GameCreationDto request = new GameCreationDto(location);
        request.getLocation().setId("507f1f77bcf86cd799439011");

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


        mockMvc.perform(post(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_CREATE)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isCreated());
    }

}
