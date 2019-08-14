package backend.services.usercontroller;

import backend.config.ContextPaths;
import backend.config.ProfileTypes;
import backend.config.oauth2.UsersRoles;
import backend.config.startup.StartupConfig;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.UserRepository;
import backend.exceptions.ExceptionDescriptions;
import backend.exceptions.ExceptionMessages;
import backend.models.response.ExceptionResponse;
import backend.models.response.Response;
import backend.models.response.ResponseMessages;
import backend.models.response.user.UserListResponseDto;
import backend.services.UserService;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * show all signed users tests
 *
 * @author Piotr Kuglin
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserService.class)
@ActiveProfiles(value = ProfileTypes.DEVELOPMENT_PROFILE)
public class ShowAllSignedUsersTest {

    private static Gson gson = new Gson();
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_return_user_list() throws Exception {

        List<UserEntity> userList = new ArrayList<>();
        userList.add(new UserEntity("testtest", "testpassword", "mail@mail.com", UsersRoles.USER));
        userList.add(new UserEntity("admin", "adminadmin", "admin@admin.pl", UsersRoles.ADMIN));

        Mockito.when(userRepository.findAll()).thenReturn(userList);

        userList.forEach(userEntity -> userEntity.setPassword(StartupConfig.HASHED_PASSWORD_REPLACEMENT));

        UserListResponseDto response = new UserListResponseDto(Response.MessageType.STATUS, ResponseMessages.LIST_OF_USERS_SHOWN, userList);
        
        mockMvc.perform(get(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_GET_ALL_USERS)).andExpect(content().json(gson.toJson(response))).andExpect(status().isOk());
    }

    @Test
    public void should_return_database_error_caused_by_not_finding_any_user_in_database() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.ERROR,
                ExceptionMessages.DATABASE_ERROR, ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        Iterable<UserEntity> iterable = () -> new Iterator<UserEntity>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public UserEntity next() {
                return null;
            }
        };

        Mockito.when(userRepository.findAll()).thenReturn(iterable);

        mockMvc.perform(get(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_GET_ALL_USERS)).andExpect(content().json(gson.toJson(response))).andExpect(status().isInternalServerError());
    }
}
