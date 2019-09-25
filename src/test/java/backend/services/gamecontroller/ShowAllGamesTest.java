//package backend.services.gamecontroller;
//
//
//import backend.config.ContextPaths;
//import backend.config.ProfileTypes;
//import backend.config.oauth2.UsersRoles;
//import backend.databases.entities.GameEntity;
//import backend.databases.entities.LocationEntity;
//import backend.databases.entities.RoleEntity;
//import backend.databases.entities.UserEntity;
//import backend.databases.repositories.GameRepository;
//import backend.models.response.Response;
//import backend.models.response.ResponseMessages;
//import backend.models.response.game.GameListResponseDto;
//import backend.services.GameService;
//import com.google.gson.Gson;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Collections;
//import java.util.*;
//
//import static java.lang.Boolean.TRUE;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(GameService.class)
//@ActiveProfiles(value = ProfileTypes.TEST_PROFILE)
//public class ShowAllGamesTest {
//
//    private static Gson gson = new Gson();
//
//    @MockBean
//    private GameRepository gameRepository;
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    public void should_return_list_of_games() throws Exception {
//
//        List<GameEntity> gameList = new ArrayList<>();
//        UserEntity userOne = new UserEntity("username","password","kalimero@mail.com",
//                UsersRoles.USER,TRUE,new Date(), new Date());
//        RoleEntity roleOne = new RoleEntity();
//        RoleEntity roleTwo = new RoleEntity();
//
//        LocationEntity locationOne = new LocationEntity("locationOne",
//                userOne,"descriptionOne",Arrays.asList(roleOne,roleTwo),new Date());
//
//        gameList.add(new GameEntity(userOne, new Date(), locationOne, roleOne));
//
//        Mockito.when(gameRepository.findAll()).thenReturn(gameList);
//
//        GameListResponseDto response = new GameListResponseDto(Response.MessageType.STATUS, ResponseMessages.LIST_OF_GAMES_SHOWN, gameList);
//
//        mockMvc.perform(get(ContextPaths.GAME_MAIN_CONTEXT + ContextPaths.GAME_GET_ALL_GAMES))
//                .andExpect(content().json(gson.toJson(response)))
//                .andExpect(status().isOk());
//    }
//}
