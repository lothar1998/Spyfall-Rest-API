package backend.endpoints.controllers;

import backend.config.ProfileTypes;
import backend.config.oauth2.UsersRoles;
import backend.config.startup.StartupConfig;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.UserRepository;
import backend.endpoints.ContextPaths;
import backend.endpoints.requests.user.UserChangePasswordRequest;
import backend.endpoints.requests.user.UserCreationRequest;
import backend.endpoints.responses.Response;
import backend.endpoints.responses.user.change_password.UserChangePasswordMessages;
import backend.endpoints.responses.user.change_password.UserChangePasswordResponse;
import backend.endpoints.responses.user.creation.UserCreationMessages;
import backend.endpoints.responses.user.creation.UserCreationResponse;
import backend.endpoints.responses.user.signed_users_list.UserListResponse;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserController tests
 *
 * @author Piotr Kuglin
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@ActiveProfiles(value = ProfileTypes.DEVELOPMENT_PROFILE)
public class UserControllerTest {

    private static Gson gson = new Gson();
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createUser_should_validate_request() throws Exception {

        UserCreationResponse response = new UserCreationResponse(Response.MessageType.ERROR, UserCreationMessages.BAD_CREDENTIALS, null);

        UserCreationRequest request1 = new UserCreationRequest(null, "janko123", "abc@abc.pl");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request1))).andExpect(status().isBadRequest())
                .andExpect(content().json(gson.toJson(response)));

        UserCreationRequest request2 = new UserCreationRequest("janko123", null, "abc@abc.pl");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request2))).andExpect(status().isBadRequest())
                .andExpect(content().json(gson.toJson(response)));

        UserCreationRequest request3 = new UserCreationRequest("janko123", "janko123", null);
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request3))).andExpect(status().isBadRequest())
                .andExpect(content().json(gson.toJson(response)));

        UserCreationRequest request4 = new UserCreationRequest("jank", "janko123", "abc@abc.pl");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request4))).andExpect(status().isBadRequest())
                .andExpect(content().json(gson.toJson(response)));

        UserCreationRequest request5 = new UserCreationRequest("janko", "jank3", "abc@abc.pl");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request5))).andExpect(status().isBadRequest())
                .andExpect(content().json(gson.toJson(response)));

        UserCreationRequest request6 = new UserCreationRequest("janko", "janko123", "abc@abc@.pl");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request6))).andExpect(status().isBadRequest())
                .andExpect(content().json(gson.toJson(response)));
    }

    @Test
    public void createUser_should_create_user() throws Exception {
        String credentials = "janko123";
        String email = "jan@kowalski.pl";

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded");
        Mockito.when(userRepository.save(Mockito.any(UserEntity.class))).thenAnswer((Answer<UserEntity>) invocation -> {
            Object[] args = invocation.getArguments();
            return (UserEntity) args[0];
        });

        UserCreationRequest request = new UserCreationRequest(credentials, credentials, email);
        UserCreationResponse response = new UserCreationResponse(Response.MessageType.MESSAGE, UserCreationMessages.USER_HAS_BEEN_CREATED, new UserEntity(credentials, StartupConfig.HASHED_PASSWORD_REPLACEMENT, email, UsersRoles.USER));
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request))).andExpect(status().isCreated())
                .andExpect(content().json(gson.toJson(response)));
    }

    @Test
    public void createUser_should_occur_database_error() throws Exception {
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded");
        Mockito.when(userRepository.save(Mockito.any(UserEntity.class))).thenReturn(null);

        UserCreationRequest request = new UserCreationRequest("janko123", "janko123", "jan@kowalski.pl");
        UserCreationResponse response = new UserCreationResponse(Response.MessageType.ERROR, UserCreationMessages.DATABASE_ERROR, null);
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(gson.toJson(response)));
    }

    @Test
    public void createUser_should_occur_user_already_exist_response() throws Exception {

        String credentials = "janko123";
        String email = "jan@kowalski.pl";

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded");
        Mockito.when(userRepository.save(Mockito.any(UserEntity.class))).thenAnswer((Answer<UserEntity>) invocation -> {
            Object[] args = invocation.getArguments();
            return (UserEntity) args[0];
        });

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(new UserEntity(credentials, credentials, email, UsersRoles.USER).setId(1L));
        UserCreationRequest request = new UserCreationRequest("janko123", "janko123", "jan@kowalski.pl");
        UserCreationResponse response = new UserCreationResponse(Response.MessageType.ERROR, UserCreationMessages.USER_ALREADY_EXISTS, null);
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request)))
                .andExpect(status().isConflict()).andExpect(content().json(gson.toJson(response)));
    }

    @Test
    public void changePassword_should_validate_request() throws Exception {

        UserChangePasswordResponse response = new UserChangePasswordResponse(Response.MessageType.ERROR, UserChangePasswordMessages.BAD_CREDENTIALS);

        UserChangePasswordRequest request1 = new UserChangePasswordRequest("asd", "janko123");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CHANGE_PASSWORD).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request1)))
                .andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(response)));

        UserChangePasswordRequest request2 = new UserChangePasswordRequest("janko123", "123");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CHANGE_PASSWORD).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request2)))
                .andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(response)));
    }

    @Test
    public void changePassword_should_validate_credentials_with_token() throws Exception {

        final String oldPassword = "janko123";
        final String newPassword = "janko1234";
        final String exampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjUwMjM3MjEsInVzZXJfbmFtZSI6ImphbmtvMTIzIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6Ijc3YmQwYzJkLTViNGQtNGU0YS1hNmVjLTEyMjk4OWU5YTUwZCIsImNsaWVudF9pZCI6ImNsaWVudF9pZCIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.C31mSjrCsinO-bKi_Ww6GoCSnbPmYyasTolkGp5Td-o";

        UserChangePasswordResponse response = new UserChangePasswordResponse(Response.MessageType.ERROR, UserChangePasswordMessages.BAD_CREDENTIALS);

        UserEntity foundUser = new UserEntity("janko123", passwordEncoder.encode(oldPassword + "bad credential"), "email@kowalski.pl", UsersRoles.USER).setId(1L);


        UserChangePasswordRequest request = new UserChangePasswordRequest(oldPassword, newPassword);

        //1) - incorrect oldPassword
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(foundUser);

        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CHANGE_PASSWORD).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request))
                .header("authorization", "Bearer " + exampleToken))
                .andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(response)));

        //2) - user has not been found
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(null);

        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CHANGE_PASSWORD).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request))
                .header("authorization", "Bearer " + exampleToken))
                .andExpect(status().isNotFound()).andExpect(content().json(gson.toJson(response)));
    }

    @Test
    public void changePassword_should_return_database_internal_server_error() throws Exception {
        final String oldPassword = "janko123";
        final String newPassword = "janko1234";
        final String exampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjUwMjM3MjEsInVzZXJfbmFtZSI6ImphbmtvMTIzIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6Ijc3YmQwYzJkLTViNGQtNGU0YS1hNmVjLTEyMjk4OWU5YTUwZCIsImNsaWVudF9pZCI6ImNsaWVudF9pZCIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.C31mSjrCsinO-bKi_Ww6GoCSnbPmYyasTolkGp5Td-o";

        UserChangePasswordResponse response = new UserChangePasswordResponse(Response.MessageType.ERROR, UserChangePasswordMessages.DATABASE_ERROR);

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("ENCODED_PASSWORD");
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(null);

        UserEntity foundUser = new UserEntity("janko123", passwordEncoder.encode(oldPassword), "email@kowalski.pl", UsersRoles.USER).setId(1L);

        UserChangePasswordRequest request = new UserChangePasswordRequest(oldPassword, newPassword);

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(foundUser);

        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CHANGE_PASSWORD).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request))
                .header("authorization", "Bearer " + exampleToken))
                .andExpect(status().isInternalServerError()).andExpect(content().json(gson.toJson(response)));
    }

    @Test
    public void changePassword_should_change_password() throws Exception {

        final String oldPassword = "janko123";
        final String newPassword = "janko1234";
        final String exampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjUwMjM3MjEsInVzZXJfbmFtZSI6ImphbmtvMTIzIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6Ijc3YmQwYzJkLTViNGQtNGU0YS1hNmVjLTEyMjk4OWU5YTUwZCIsImNsaWVudF9pZCI6ImNsaWVudF9pZCIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.C31mSjrCsinO-bKi_Ww6GoCSnbPmYyasTolkGp5Td-o";

        UserChangePasswordResponse response = new UserChangePasswordResponse(Response.MessageType.MESSAGE, UserChangePasswordMessages.PASSWORD_HAS_BEEN_CHANGED);

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("ENCODED_PASSWORD");
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userRepository.save(Mockito.any())).thenAnswer((Answer<UserEntity>) invocation -> {
            Object[] args = invocation.getArguments();
            return (UserEntity) args[0];
        });

        UserEntity foundUser = new UserEntity("janko123", passwordEncoder.encode(oldPassword), "email@kowalski.pl", UsersRoles.USER).setId(1L);

        UserChangePasswordRequest request = new UserChangePasswordRequest(oldPassword, newPassword);

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(foundUser);

        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CHANGE_PASSWORD).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request))
                .header("authorization", "Bearer " + exampleToken))
                .andExpect(status().isOk()).andExpect(content().json(gson.toJson(response)));
    }

    @Test
    public void showAllSignedUsers_should_return_user_list() throws Exception {

        List<UserEntity> userList = new ArrayList<>();
        userList.add(new UserEntity("testtest", "testpassword", "mail@mail.com", UsersRoles.USER));
        userList.add(new UserEntity("admin", "adminadmin", "admin@admin.pl", UsersRoles.ADMIN));

        Mockito.when(userRepository.findAll()).thenReturn(userList);

        userList.forEach(userEntity -> {
            userEntity.setPassword(StartupConfig.HASHED_PASSWORD_REPLACEMENT);
        });

        UserListResponse response = new UserListResponse(Response.MessageType.STATUS, userList);


        mockMvc.perform(get(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_GET_ALL_USERS)).andExpect(content().json(gson.toJson(response))).andExpect(status().isOk());
    }

    @Test
    public void showAllSignedUsers_should_return_database_error() throws Exception {
        UserListResponse response = new UserListResponse(Response.MessageType.ERROR, null);

        Mockito.when(userRepository.findAll()).thenReturn(null);

        mockMvc.perform(get(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_GET_ALL_USERS)).andExpect(content().json(gson.toJson(response))).andExpect(status().isInternalServerError());
    }
}