package backend.services.locationservice;

import backend.config.ContextPaths;
import backend.config.ProfileTypes;
import backend.config.oauth2.UsersRoles;
import backend.databases.entities.LocationEntity;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.LocationRepository;
import backend.databases.repositories.RoleRepository;
import backend.databases.repositories.UserRepository;
import backend.exceptions.ExceptionDescriptions;
import backend.exceptions.ExceptionMessages;
import backend.models.response.ExceptionResponse;
import backend.models.response.Response;
import backend.models.response.ResponseMessages;
import backend.models.response.location.LocationDeletionResponseDto;
import backend.parsers.JwtDecoder;
import backend.parsers.Parser;
import backend.parsers.UsernameParser;
import backend.services.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * delete location by id tests
 *
 * @author Piotr Kuglin
 */
@RunWith(SpringRunner.class)
@WebMvcTest(LocationService.class)
@ActiveProfiles(value = ProfileTypes.TEST_PROFILE)
public class DeleteLocationByIdTest {

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

    private Parser<String> usernameParser = new UsernameParser(new JwtDecoder());

    @Test
    public void should_occur_not_found_exception_caused_by_wrong_id() throws Exception {
        ExceptionResponse bodyOfResponse = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.LOCATION_NOT_FOUND, ExceptionDescriptions.NOT_FOUND, HttpStatus.NOT_FOUND);

        Mockito.when(locationRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(delete(ContextPaths.LOCATION_MAIN_CONTEXT + "/5d87c214857aba0001625f7a").header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isNotFound()).andExpect(content().json(objectMapper.writeValueAsString(bodyOfResponse)));
    }

    @Test
    public void should_occur_database_exception_caused_user_not_found() throws Exception {
        ExceptionResponse bodyOfResponse = new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.DATABASE_ERROR, ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(locationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(new LocationEntity()));
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(null);

        mockMvc.perform(delete(ContextPaths.LOCATION_MAIN_CONTEXT + "/5d87c214857aba0001625f7a").header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isInternalServerError()).andExpect(content().json(objectMapper.writeValueAsString(bodyOfResponse)));
    }

    @Test
    public void should_occur_permission_denied_exception_caused_by_user_has_no_permission_to_delete_location() throws Exception {
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

        mockMvc.perform(delete(ContextPaths.LOCATION_MAIN_CONTEXT + "/5d87c214857aba0001625f7a").header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isForbidden()).andExpect(content().json(objectMapper.writeValueAsString(bodyOfResponse)));
    }

    @Test
    public void should_delete_location_properly() throws Exception {
        LocationDeletionResponseDto response = new LocationDeletionResponseDto(Response.MessageType.INFO, ResponseMessages.LOCATION_DELETION);

        UserEntity user = new UserEntity("janko123", "janko123", "email@email.com", UsersRoles.USER, true, null, null);
        user.setId("5d87c214857aba0001625aaa");
        LocationEntity location = new LocationEntity("testname", user, "testdescription", null, null);

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });
        Mockito.when(locationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(location));
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(user);

        mockMvc.perform(delete(ContextPaths.LOCATION_MAIN_CONTEXT + "/5d87c214857aba0001625f7a").header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isOk()).andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}
