package backend.services.usercontroller;

import backend.config.ContextPaths;
import backend.config.ProfileTypes;
import backend.config.oauth2.UsersRoles;
import backend.config.startup.StartupConfig;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.UserRepository;
import backend.exceptions.ExceptionDescriptions;
import backend.exceptions.ExceptionMessages;
import backend.models.request.user.UserCreationDto;
import backend.models.response.ExceptionResponse;
import backend.models.response.Response;
import backend.models.response.ResponseMessages;
import backend.models.response.user.UserCreationResponseDto;
import backend.services.UserService;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@ActiveProfiles(value = ProfileTypes.DEVELOPMENT_PROFILE)
public class CreateUserTest {

    private static Gson gson = new Gson();
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    private ExceptionResponse responseValidationError;
    private ExceptionResponse responseDatabaseError;
    private ExceptionResponse responseUserAlreadyExistsError;


    @Before
    public void expectedResponseSetup() {
        this.responseValidationError = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.VALIDATION_ERROR, ExceptionDescriptions.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        this.responseDatabaseError =
                new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.DATABASE_ERROR, ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        this.responseUserAlreadyExistsError = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.USER_ALREADY_EXISTS, ExceptionDescriptions.BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void should_validate_request_null_username() throws Exception {
        UserCreationDto request = new backend.models.request.user.UserCreationDto(null, "janko123", "abc@abc.pl");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(request))).andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(responseValidationError)));
    }

    @Test
    public void should_validate_request_blank_username() throws Exception {
        UserCreationDto request = new backend.models.request.user.UserCreationDto("", "janko123", "abc@abc.pl");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(request))).andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(responseValidationError)));
    }

    @Test
    public void should_validate_request_too_short_username() throws Exception {
        UserCreationDto request = new backend.models.request.user.UserCreationDto("jank", "janko123", "abc@abc.pl");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(request))).andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(responseValidationError)));
    }

    @Test
    public void should_validate_request_null_password() throws Exception {
        UserCreationDto request = new backend.models.request.user.UserCreationDto("janko123", null, "abc@abc.pl");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(request))).andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(responseValidationError)));
    }

    @Test
    public void should_validate_request_blank_password() throws Exception {
        UserCreationDto request = new backend.models.request.user.UserCreationDto("janko123", "", "abc@abc.pl");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(request))).andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(responseValidationError)));
    }

    @Test
    public void should_validate_request_too_short_password() throws Exception {
        UserCreationDto request = new backend.models.request.user.UserCreationDto("janko123", "janko12", "abc@abc.pl");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(request))).andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(responseValidationError)));
    }

    @Test
    public void should_validate_request_null_email() throws Exception {
        UserCreationDto request = new backend.models.request.user.UserCreationDto("janko123", "janko123", null);
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(request))).andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(responseValidationError)));
    }

    @Test
    public void should_validate_request_blank_email() throws Exception {
        UserCreationDto request = new backend.models.request.user.UserCreationDto("janko123", "janko123", "");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(request))).andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(responseValidationError)));
    }

    @Test
    public void should_validate_request_not_email_pattern() throws Exception {
        UserCreationDto request = new backend.models.request.user.UserCreationDto("janko123", "janko123", "jan@kro@.place");
        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(request))).andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(responseValidationError)));
    }

    @Test
    public void should_occur_database_error() throws Exception {
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded");
        Mockito.when(userRepository.save(Mockito.any(UserEntity.class))).thenReturn(new UserEntity("a", "b", "c", "d"));

        UserCreationDto request = new backend.models.request.user.UserCreationDto("janko123", "janko123", "jan@kowalski.pl");

        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(gson.toJson(responseDatabaseError)));
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

        UserEntity savedUser = new UserEntity(credentials, credentials, email, UsersRoles.USER);
        savedUser.setId(1L);

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(savedUser);

        UserCreationDto request = new backend.models.request.user.UserCreationDto("janko123", "janko123", "jan@kowalski.pl");

        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(request)))
                .andExpect(status().isBadRequest()).andExpect(content().json(gson.toJson(responseUserAlreadyExistsError)));
    }

    @Test
    public void should_create_user() throws Exception {
        String credentials = "janko123";
        String email = "jan@kowalski.pl";

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded");
        Mockito.when(userRepository.save(Mockito.any(UserEntity.class))).thenAnswer((Answer<UserEntity>) invocation -> {
            Object[] args = invocation.getArguments();
            return (UserEntity) args[0];
        });

        UserCreationDto request = new backend.models.request.user.UserCreationDto(credentials, credentials, email);

        UserCreationResponseDto response = new UserCreationResponseDto(Response.MessageType.INFO, ResponseMessages.USER_HAS_BEEN_CREATED,
                new UserEntity(credentials, StartupConfig.HASHED_PASSWORD_REPLACEMENT, email, UsersRoles.USER));

        mockMvc.perform(post(ContextPaths.USER_MAIN_CONTEXT + ContextPaths.USER_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(gson.toJson(request))).andExpect(status().isCreated())
                .andExpect(content().json(gson.toJson(response)));
    }
}
