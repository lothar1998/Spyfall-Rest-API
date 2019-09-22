package backend.services.locationservice;

import backend.config.ContextPaths;
import backend.config.ProfileTypes;
import backend.config.startup.StartupConfig;
import backend.databases.entities.LocationEntity;
import backend.databases.entities.RoleEntity;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.LocationRepository;
import backend.databases.repositories.RoleRepository;
import backend.databases.repositories.UserRepository;
import backend.exceptions.ExceptionDescriptions;
import backend.exceptions.ExceptionMessages;
import backend.models.request.location.LocationCreationDto;
import backend.models.response.ExceptionResponse;
import backend.models.response.Response;
import backend.services.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Calendar;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * create location tests
 *
 * @author Piotr Kuglin
 */
@RunWith(SpringRunner.class)
@WebMvcTest(LocationService.class)
@ActiveProfiles(value = ProfileTypes.TEST_PROFILE)
public class CreateLocationTest {

    private final static String exampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjUwMjM3MjEsInVzZXJfbmFtZSI6ImphbmtvMTIzIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6Ijc3YmQwYzJkLTViNGQtNGU0YS1hNmVjLTEyMjk4OWU5YTUwZCIsImNsaWVudF9pZCI6ImNsaWVudF9pZCIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.C31mSjrCsinO-bKi_Ww6GoCSnbPmYyasTolkGp5Td-o";
    private static ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private LocationRepository locationRepository;
    @MockBean
    private RoleRepository roleRepository;
    @Autowired
    private MockMvc mockMvc;
    private ExceptionResponse responseValidationException;

    @Before
    public void setUp() {
        this.responseValidationException = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.VALIDATION_ERROR, ExceptionDescriptions.BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void should_validate_name_is_not_null() throws Exception {
        LocationCreationDto request = new LocationCreationDto(null, "test", Collections.singletonList(new RoleEntity()));

        mockMvc.perform(post(ContextPaths.LOCATION_MAIN_CONTEXT + ContextPaths.LOCATION_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isBadRequest()).andExpect(content().json(objectMapper.writeValueAsString(responseValidationException)));
    }

    @Test
    public void should_validate_name_is_not_blank() throws Exception {
        LocationCreationDto request = new LocationCreationDto("", "test", Collections.singletonList(new RoleEntity()));

        mockMvc.perform(post(ContextPaths.LOCATION_MAIN_CONTEXT + ContextPaths.LOCATION_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isBadRequest()).andExpect(content().json(objectMapper.writeValueAsString(responseValidationException)));
    }

    @Test
    public void should_validate_name_is_proper_size() throws Exception {

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < StartupConfig.LOCATION_NAME_MIN_LENGTH - 1; i++)
            stringBuilder.append("a");

        LocationCreationDto request = new LocationCreationDto(stringBuilder.toString(), "test", Collections.singletonList(new RoleEntity()));

        mockMvc.perform(post(ContextPaths.LOCATION_MAIN_CONTEXT + ContextPaths.LOCATION_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isBadRequest()).andExpect(content().json(objectMapper.writeValueAsString(responseValidationException)));
    }

    @Test
    public void should_validate_description_is_not_null() throws Exception {
        LocationCreationDto request = new LocationCreationDto("nameOfLocation", null, Collections.singletonList(new RoleEntity()));

        mockMvc.perform(post(ContextPaths.LOCATION_MAIN_CONTEXT + ContextPaths.LOCATION_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isBadRequest()).andExpect(content().json(objectMapper.writeValueAsString(responseValidationException)));
    }

    @Test
    public void should_validate_roles_is_not_null() throws Exception {
        LocationCreationDto request = new LocationCreationDto("nameOfLocation", "test", null);

        mockMvc.perform(post(ContextPaths.LOCATION_MAIN_CONTEXT + ContextPaths.LOCATION_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isBadRequest()).andExpect(content().json(objectMapper.writeValueAsString(responseValidationException)));
    }

    @Test
    public void should_occur_database_error_caused_by_owner_not_found() throws Exception {
        LocationCreationDto request = new LocationCreationDto("nameOfLocation", "test", Collections.singletonList(new RoleEntity()));

        ExceptionResponse response = new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.DATABASE_ERROR, ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(null);

        mockMvc.perform(post(ContextPaths.LOCATION_MAIN_CONTEXT + ContextPaths.LOCATION_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isInternalServerError()).andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_occur_database_error_caused_by_location_saving_error() throws Exception {
        LocationCreationDto request = new LocationCreationDto("nameOfLocation", "test", Collections.singletonList(new RoleEntity()));

        ExceptionResponse response = new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.DATABASE_ERROR, ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(locationRepository.save(Mockito.any())).thenReturn(null);

        mockMvc.perform(post(ContextPaths.LOCATION_MAIN_CONTEXT + ContextPaths.LOCATION_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isInternalServerError()).andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_save_location_properly() throws Exception {
        LocationCreationDto request = new LocationCreationDto("nameOfLocation", "test", Collections.singletonList(new RoleEntity()));

        UserEntity userEntity = new UserEntity("username", "password", "email@email.com", "ADMIN",
                true, Calendar.getInstance().getTime(), Calendar.getInstance().getTime());

        userEntity.setId("507f1f77bcf86cd799439011");

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(userEntity);
        Mockito.when(locationRepository.save(Mockito.any())).thenAnswer((Answer<LocationEntity>) invocation -> {
            Object[] args = invocation.getArguments();
            return (LocationEntity) args[0];
        });

        mockMvc.perform(post(ContextPaths.LOCATION_MAIN_CONTEXT + ContextPaths.LOCATION_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isCreated());
    }
}
