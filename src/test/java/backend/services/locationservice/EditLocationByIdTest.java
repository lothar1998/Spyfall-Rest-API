package backend.services.locationservice;

import backend.config.ContextPaths;
import backend.config.ProfileTypes;
import backend.config.oauth2.UsersRoles;
import backend.config.startup.StartupConfig;
import backend.databases.entities.LocationEntity;
import backend.databases.entities.RoleEntity;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.LocationRepository;
import backend.databases.repositories.RoleRepository;
import backend.databases.repositories.UserRepository;
import backend.exceptions.ExceptionDescriptions;
import backend.exceptions.ExceptionMessages;
import backend.models.request.location.LocationSchemaDto;
import backend.models.response.ExceptionResponse;
import backend.models.response.Response;
import backend.parsers.JwtDecoder;
import backend.parsers.Parser;
import backend.parsers.UsernameParser;
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

import java.util.Collections;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * edit location by id tests
 *
 * @author Piotr Kuglin
 */
@RunWith(SpringRunner.class)
@WebMvcTest(LocationService.class)
@ActiveProfiles(value = ProfileTypes.TEST_PROFILE)
public class EditLocationByIdTest {

    private final static String exampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjUwMjM3MjEsInVzZXJfbmFtZSI6ImphbmtvMTIzIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6Ijc3YmQwYzJkLTViNGQtNGU0YS1hNmVjLTEyMjk4OWU5YTUwZCIsImNsaWVudF9pZCI6ImNsaWVudF9pZCIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.C31mSjrCsinO-bKi_Ww6GoCSnbPmYyasTolkGp5Td-o";
    private static ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private LocationRepository locationRepository;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private Parser<String> parser;
    @Autowired
    private MockMvc mockMvc;

    private ExceptionResponse responseValidationException;
    private Parser<String> usernameParser = new UsernameParser(new JwtDecoder());

    @Before
    public void setUp() {
        this.responseValidationException = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.VALIDATION_ERROR, ExceptionDescriptions.BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void should_validate_name_is_not_null() throws Exception {
        LocationSchemaDto request = new LocationSchemaDto(null, "test", Collections.singletonList(new RoleEntity()));

        mockMvc.perform(put(ContextPaths.LOCATION_MAIN_CONTEXT + "/5d87c214857aba0001625f7a").contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isBadRequest()).andExpect(content().json(objectMapper.writeValueAsString(responseValidationException)));
    }

    @Test
    public void should_validate_name_is_not_blank() throws Exception {
        LocationSchemaDto request = new LocationSchemaDto("", "test", Collections.singletonList(new RoleEntity()));

        mockMvc.perform(put(ContextPaths.LOCATION_MAIN_CONTEXT + "/5d87c214857aba0001625f7a").contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isBadRequest()).andExpect(content().json(objectMapper.writeValueAsString(responseValidationException)));
    }

    @Test
    public void should_validate_name_is_proper_size() throws Exception {

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < StartupConfig.LOCATION_NAME_MIN_LENGTH - 1; i++)
            stringBuilder.append("a");

        LocationSchemaDto request = new LocationSchemaDto(stringBuilder.toString(), "test", Collections.singletonList(new RoleEntity()));

        mockMvc.perform(put(ContextPaths.LOCATION_MAIN_CONTEXT + "/5d87c214857aba0001625f7a").contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isBadRequest()).andExpect(content().json(objectMapper.writeValueAsString(responseValidationException)));
    }

    @Test
    public void should_validate_description_is_not_null() throws Exception {
        LocationSchemaDto request = new LocationSchemaDto("nameOfLocation", null, Collections.singletonList(new RoleEntity()));

        mockMvc.perform(put(ContextPaths.LOCATION_MAIN_CONTEXT + "/5d87c214857aba0001625f7a").contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isBadRequest()).andExpect(content().json(objectMapper.writeValueAsString(responseValidationException)));
    }

    @Test
    public void should_validate_roles_is_not_null() throws Exception {
        LocationSchemaDto request = new LocationSchemaDto("nameOfLocation", "test", null);

        mockMvc.perform(put(ContextPaths.LOCATION_MAIN_CONTEXT + "/5d87c214857aba0001625f7a").contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isBadRequest()).andExpect(content().json(objectMapper.writeValueAsString(responseValidationException)));
    }

    @Test
    public void should_occur_not_found_exception_caused_by_wrong_id() throws Exception {
        LocationSchemaDto request = new LocationSchemaDto("nameOfLocation", "test", Collections.singletonList(new RoleEntity()));
        ExceptionResponse bodyOfResponse = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.LOCATION_NOT_FOUND, ExceptionDescriptions.NOT_FOUND, HttpStatus.NOT_FOUND);

        Mockito.when(locationRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(put(ContextPaths.LOCATION_MAIN_CONTEXT + "/5d87c214857aba0001625f7a").contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isNotFound()).andExpect(content().json(objectMapper.writeValueAsString(bodyOfResponse)));
    }

    @Test
    public void should_occur_database_exception_caused_user_not_found() throws Exception {
        LocationSchemaDto request = new LocationSchemaDto("nameOfLocation", "test", Collections.singletonList(new RoleEntity()));
        ExceptionResponse bodyOfResponse = new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.DATABASE_ERROR, ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(parser.parse(Mockito.anyString())).thenReturn("janko123");
        Mockito.when(locationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(new LocationEntity()));
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(null);

        mockMvc.perform(put(ContextPaths.LOCATION_MAIN_CONTEXT + "/5d87c214857aba0001625f7a").contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isInternalServerError()).andExpect(content().json(objectMapper.writeValueAsString(bodyOfResponse)));
    }

    @Test
    public void should_occur_permission_denied_exception_caused_by_user_has_no_permission_to_edit_location() throws Exception {
        LocationSchemaDto request = new LocationSchemaDto("nameOfLocation", "test", Collections.singletonList(new RoleEntity()));
        ExceptionResponse bodyOfResponse = new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.DELETION_VALIDATION_ERROR, ExceptionDescriptions.PERMISSION_DENIED, HttpStatus.FORBIDDEN);

        UserEntity user = new UserEntity("janko123", "janko123", "email@email.com", UsersRoles.USER, true, null, null);
        user.setId("5d87c214857aba0001625aaa");
        LocationEntity location = new LocationEntity("testname", user, "testdescription", null, null);

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });
        Mockito.when(locationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(location));
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(new UserEntity());

        mockMvc.perform(put(ContextPaths.LOCATION_MAIN_CONTEXT + "/5d87c214857aba0001625f7a").contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isForbidden()).andExpect(content().json(objectMapper.writeValueAsString(bodyOfResponse)));
    }

    @Test
    public void should_occur_database_error_caused_by_location_saving_error() throws Exception {
        LocationSchemaDto request = new LocationSchemaDto("nameOfLocation", "test", Collections.singletonList(new RoleEntity()));
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.DATABASE_ERROR, ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        UserEntity user = new UserEntity("janko123", "janko123", "email@email.com", UsersRoles.USER, true, null, null);
        user.setId("5d87c214857aba0001625aaa");
        LocationEntity location = new LocationEntity("testname", user, "testdescription", null, null);

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });
        Mockito.when(locationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(location));
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(user);
        Mockito.when(locationRepository.save(Mockito.any())).thenReturn(new LocationEntity());

        mockMvc.perform(put(ContextPaths.LOCATION_MAIN_CONTEXT + ContextPaths.LOCATION_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isInternalServerError()).andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_properly_edit_location() throws Exception {
        LocationSchemaDto request = new LocationSchemaDto("nameOfLocation", "test", Collections.singletonList(new RoleEntity()));
        ExceptionResponse response = new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.DATABASE_ERROR, ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        UserEntity user = new UserEntity("janko123", "janko123", "email@email.com", UsersRoles.USER, true, null, null);
        user.setId("5d87c214857aba0001625aaa");
        LocationEntity location = new LocationEntity("testname", user, "testdescription", null, null);

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });
        Mockito.when(locationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(location));
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(user);
        Mockito.when(locationRepository.save(Mockito.any())).thenAnswer((Answer<LocationEntity>) invocation -> {
            Object[] args = invocation.getArguments();
            return (LocationEntity) args[0];
        });

        mockMvc.perform(put(ContextPaths.LOCATION_MAIN_CONTEXT + ContextPaths.LOCATION_CREATE).contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isOk());
    }

}
