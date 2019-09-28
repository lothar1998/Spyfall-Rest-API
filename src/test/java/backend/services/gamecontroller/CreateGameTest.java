package backend.services.gamecontroller;

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
import backend.models.request.game.GameCreationDto;
import backend.models.response.ExceptionResponse;
import backend.models.response.Response;
import backend.parsers.*;
import backend.services.GameService;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.util.Arrays;
import java.util.Date;

import static java.lang.Boolean.TRUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private RoleRepository roleRepository;
    @MockBean
    private LocationRepository locationRepository;
    @MockBean
    private GameRepository gameRepository;
    @MockBean
    private Parser<String> parser;
    @Autowired
    private MockMvc mockMvc;
    private ExceptionResponse exceptionResponse;
    private Parser<String> usernameParser = new UsernameParser(new JwtDecoder());


    @Before
    public void startingSetUp() {
        this.exceptionResponse = new ExceptionResponse(Response.MessageType.WARNING,
                ExceptionMessages.DATABASE_ERROR, ExceptionDescriptions.BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }

//TODO: complete test
    @Test
    public void should_create_new_game() throws Exception {
        UserEntity userOne = new UserEntity("owner","password","kalimero@mail.com",
                UsersRoles.USER,TRUE,new Date(), new Date());
        userOne.setId("507f1f77bcf86cd799439011");

        LocationEntity location = new LocationEntity("Location",userOne,"desc", Arrays.asList(new RoleEntity(),new RoleEntity()), new Date());
        location.setId("507f1f77bcf86cd799439010");

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });

        GameCreationDto request = new GameCreationDto(location);

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(userOne);
        Mockito.when(gameRepository.save(Mockito.any())).thenAnswer((Answer<GameEntity>) invocation -> {
           Object[] args = invocation.getArguments();
           return (GameEntity) args[0];
        });

        mockMvc.perform(post(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_CREATE)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isCreated());
    }

}
