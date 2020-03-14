package backend.services.gamecontroller;

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
import backend.parsers.Parser;
import backend.services.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(GameService.class)
@ActiveProfiles(value = ProfileTypes.TEST_PROFILE)
public class ShowGameLobbyTest {

    private final static String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJleHAiOjE1NjkyNjYxNjQsInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiJdLCJqdGkiOiIzYjlmYWVmMi0zMDVjLTRkY2UtOGNhMC0wYzEyMWYwYTA1ZGIiLCJjbGllbnRfaWQiOiJjbGllbnRfaWQiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXX0" +
            ".4-CyQK6tkXyH5YKvoAKSyrr1Pp5nWuG7mNLk8gFheLw";
    private final static String gameIdUri = "/507f1f77bcf86cd799439011";
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

    @Mock
    private UserEntity user;
    @Mock
    private RoleEntity role;
    @InjectMocks
    private LocationEntity locationEntity;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_occur_not_found_exception_caused_by_not_correct_game_id() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.WARNING,
                ExceptionMessages.GAME_NOT_FOUND, ExceptionDescriptions.NOT_FOUND, HttpStatus.NOT_FOUND);

        Mockito.when(gameRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get(ContextPaths.GAME_MAIN_CONTEXT + "/507f1f77bcf86cd799439011", "507f1f77bcf86cd799439011")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_occur_game_in_progress_exception() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.WARNING,
                ExceptionMessages.GAME_IN_PROGRESS, ExceptionDescriptions.PERMISSION_DENIED, HttpStatus.FORBIDDEN);

        GameEntity gameEntity = new GameEntity(user, Date.from(Instant.EPOCH), locationEntity,
                Collections.singletonMap("User", new RoleEntity()));
        gameEntity.setGameStarted(true);
        gameEntity.setGameDisabled(false);

        Mockito.when(gameRepository.findById(Mockito.anyString())).thenReturn(Optional.of(gameEntity));

        mockMvc.perform(get(ContextPaths.GAME_MAIN_CONTEXT + "/507f1f77bcf86cd799439011", "507f1f77bcf86cd799439011")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_show_game_lobby() throws Exception {
        Mockito.when(gameRepository.findById(Mockito.anyString())).thenReturn(Optional.of(new GameEntity()));

        mockMvc.perform(get(ContextPaths.GAME_MAIN_CONTEXT + "/507f1f77bcf86cd799439011", "507f1f77bcf86cd799439011")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

}
