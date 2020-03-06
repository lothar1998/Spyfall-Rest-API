package backend.services.locationservice;

import backend.config.ContextPaths;
import backend.config.ProfileTypes;
import backend.config.oauth2.UsersRoles;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.LocationRepository;
import backend.databases.repositories.RoleRepository;
import backend.databases.repositories.UserRepository;
import backend.exceptions.ExceptionDescriptions;
import backend.exceptions.ExceptionMessages;
import backend.models.response.ExceptionResponse;
import backend.models.response.Response;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * get all user's location by username tests
 *
 * @author Piotr Kuglin
 */
@RunWith(SpringRunner.class)
@WebMvcTest(LocationService.class)
@ActiveProfiles(value = ProfileTypes.TEST_PROFILE)
public class GetAllLocationsByUsernameTest {

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
    public void should_occur_error_caused_by_owner_not_found() throws Exception {

        ExceptionResponse response = new ExceptionResponse(Response.MessageType.ERROR, ExceptionMessages.DATABASE_ERROR, ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(null);

        mockMvc.perform(get(ContextPaths.LOCATION_MAIN_CONTEXT + ContextPaths.LOCATION_GET_ALL_BY_USERNAME).header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isInternalServerError()).andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void should_return_list_of_locations_by_username_properly() throws Exception {

        UserEntity owner = new UserEntity("janko123", "janko123", "mail@mail.com", UsersRoles.USER, true, null, null);

        Mockito.when(parser.parse(Mockito.anyString())).thenAnswer((Answer<String>) invocation -> {
            Object[] args = invocation.getArguments();
            return usernameParser.parse((String) args[0]);
        });
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(owner);

        mockMvc.perform(get(ContextPaths.LOCATION_MAIN_CONTEXT + ContextPaths.LOCATION_GET_ALL_BY_USERNAME).header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isOk());
    }
}
