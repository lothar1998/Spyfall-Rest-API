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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for checking listing all games that user hosts
 * @author kamkalis
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GameService.class)
@ActiveProfiles(value = ProfileTypes.TEST_PROFILE)
public class ShowHostGamesTest {
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
    public void should_occur_database_exception_caused_by_user_not_found() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.DATABASE_ERROR,
                ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(null);

        mockMvc.perform(get(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_GET_ALL + ContextPaths.GAME_USER)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_return_user_games_list() throws Exception {
        UserEntity user = new UserEntity("janko123", "janko123", "mail@mail.com", UsersRoles.USER, true, null, null);
        user.setId("507f1f77bcf86cd799439011");

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(user);

        List<GameEntity> gameList = new ArrayList<>();
        gameList.add(new GameEntity(user, Date.from(Instant.EPOCH), locationEntity, Collections.singletonMap("user", new RoleEntity())));

        Mockito.when(gameRepository.getGameEntityByHost(user)).thenReturn(gameList);

        mockMvc.perform(get(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_GET_ALL + ContextPaths.GAME_USER)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }
}
