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
import backend.parsers.Parser;
import backend.services.GameService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for checking the game listing
 * @author kamkalis
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GameService.class)
@ActiveProfiles(value = ProfileTypes.TEST_PROFILE)
public class ShowAllGamesTest {

    private final static String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJleHAiOjE1NjkyNjYxNjQsInVzZXJfbmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiJdLCJqdGkiOiIzYjlmYWVmMi0zMDVjLTRkY2UtOGNhMC0wYzEyMWYwYTA1ZGIiLCJjbGllbnRfaWQiOiJjbGllbnRfaWQiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXX0" +
            ".4-CyQK6tkXyH5YKvoAKSyrr1Pp5nWuG7mNLk8gFheLw";

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

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_return_list_of_games() throws Exception {
        List<GameEntity> list = new ArrayList<>();

        UserEntity user1 = new UserEntity("janko123", "janko123", "mail@mail.com",
                UsersRoles.USER, true, null, null);

        UserEntity user2 = new UserEntity("janko1234", "janko1234", "mail2@mail.com",
                UsersRoles.USER, true, null, null);

        LocationEntity location = new LocationEntity("AGH", user1, "Coronavirus",
                Collections.singletonList(new RoleEntity()), Date.from(Instant.EPOCH));

        GameEntity game1 = new GameEntity(user1, Date.from(Instant.EPOCH), location,
                Collections.singletonMap("User1", new RoleEntity()));
        GameEntity game2 = new GameEntity(user2, Date.from(Instant.EPOCH), location,
                Collections.singletonMap("User2", new RoleEntity()));

        list.add(game1);
        list.add(game2);

        Mockito.when(gameRepository.findAll()).thenReturn(list);

        mockMvc.perform(get(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_GET_ALL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }
}
