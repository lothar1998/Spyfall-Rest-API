package backend.services.usercontroller;

import backend.config.ContextPaths;
import backend.config.ProfileTypes;
import backend.config.oauth2.UsersRoles;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.UserRepository;
import backend.exceptions.ExceptionDescriptions;
import backend.exceptions.ExceptionMessages;
import backend.models.request.user.PasswordChangeDto;
import backend.models.response.ExceptionResponse;
import backend.models.response.Response;
import backend.models.response.ResponseMessages;
import backend.models.response.user.PasswordChangeResponseDto;
import backend.services.UserService;
import com.google.gson.Gson;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * create user tests
 *
 * @author Piotr Kuglin
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserService.class)
@ActiveProfiles(value = ProfileTypes.TEST_PROFILE)
public class ChangePasswordTest {

    private static Gson gson = new Gson();
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    private final static String exampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjUwMjM3MjEsInVzZXJfbmFtZSI6ImphbmtvMTIzIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6Ijc3YmQwYzJkLTViNGQtNGU0YS1hNmVjLTEyMjk4OWU5YTUwZCIsImNsaWVudF9pZCI6ImNsaWVudF9pZCIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.C31mSjrCsinO-bKi_Ww6GoCSnbPmYyasTolkGp5Td-o";

    @Test
    public void should_validate_oldPassword_size() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.VALIDATION_ERROR, ExceptionDescriptions.BAD_REQUEST, HttpStatus.BAD_REQUEST);

        PasswordChangeDto request = new PasswordChangeDto("asd", "janko123");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CHANGE_PASSWORD).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(response)));
    }

    @Test
    public void should_validate_newPassword_size() throws Exception {
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.VALIDATION_ERROR, ExceptionDescriptions.BAD_REQUEST, HttpStatus.BAD_REQUEST);

        PasswordChangeDto request = new PasswordChangeDto("janko123", "123");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CHANGE_PASSWORD).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(response)));
    }

    @Test
    public void should_validate_username_in_database() throws Exception {
        final String oldPassword = "janko123";
        final String newPassword = "janko1234";

        PasswordChangeDto request = new PasswordChangeDto(oldPassword, newPassword);

        ExceptionResponse response = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.VALIDATION_ERROR, ExceptionDescriptions.BAD_REQUEST, HttpStatus.BAD_REQUEST);

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(null);

        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CHANGE_PASSWORD).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(response)));
    }

    @Test
    public void should_validate_oldPassword() throws Exception {
        final String oldPassword = "janko123";
        final String newPassword = "janko1234";

        PasswordChangeDto request = new PasswordChangeDto(oldPassword, newPassword);

        ExceptionResponse response = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.VALIDATION_ERROR, ExceptionDescriptions.BAD_REQUEST, HttpStatus.BAD_REQUEST);

        UserEntity foundUser = new UserEntity("janko123", passwordEncoder.encode(oldPassword + "bad credential"), "email@kowalski.pl", UsersRoles.USER, true, null, null);
        foundUser.setId("507f1f77bcf86cd799439011");

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(foundUser);

        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CHANGE_PASSWORD).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(response)));
    }

    @Test
    public void should_check_updated_user() throws Exception {
        final String oldPassword = "janko123";
        final String newPassword = "janko1234";

        ExceptionResponse response = new ExceptionResponse(Response.MessageType.ERROR,
                ExceptionMessages.DATABASE_ERROR, ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("ENCODED_PASSWORD");
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(null);

        UserEntity foundUser = new UserEntity("janko123", passwordEncoder.encode(oldPassword), "email@kowalski.pl", UsersRoles.USER, true, null, null);
        foundUser.setId("507f1f77bcf86cd799439011");

        PasswordChangeDto request = new PasswordChangeDto(oldPassword, newPassword);

        UserEntity savedUser = new UserEntity(foundUser.getUsername(), foundUser.getPassword(), foundUser.getEmail(), foundUser.getAuthority(), foundUser.isEnabled(), foundUser.getSignedDate(), foundUser.getLastLogged());
        savedUser.setPassword("abc");

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(foundUser);
        Mockito.when(userRepository.save(Mockito.any(UserEntity.class))).thenReturn(savedUser);
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CHANGE_PASSWORD).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isInternalServerError()).andExpect(content().json(gson.toJson(response)));
    }

    @Test
    public void should_change_password() throws Exception {

        final String oldPassword = "janko123";
        final String newPassword = "janko1234";

        PasswordChangeResponseDto response = new PasswordChangeResponseDto(Response.MessageType.INFO, ResponseMessages.PASSWORD_HAS_BEEN_CHANGED);

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("ENCODED_PASSWORD");
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userRepository.save(Mockito.any())).thenAnswer((Answer<UserEntity>) invocation -> {
            Object[] args = invocation.getArguments();
            return (UserEntity) args[0];
        });

        UserEntity foundUser = new UserEntity("janko123", passwordEncoder.encode(oldPassword), "email@kowalski.pl", UsersRoles.USER, true, null, null);
        foundUser.setId("507f1f77bcf86cd799439011");

        PasswordChangeDto request = new PasswordChangeDto(oldPassword, newPassword);

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(foundUser);

        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CHANGE_PASSWORD).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isOk()).andExpect(content().json(gson.toJson(response)));
    }
}
