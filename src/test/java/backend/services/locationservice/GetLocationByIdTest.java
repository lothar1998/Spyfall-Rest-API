package backend.services.locationservice;

import backend.config.ContextPaths;
import backend.config.ProfileTypes;
import backend.databases.entities.LocationEntity;
import backend.databases.repositories.LocationRepository;
import backend.databases.repositories.RoleRepository;
import backend.databases.repositories.UserRepository;
import backend.exceptions.ExceptionDescriptions;
import backend.exceptions.ExceptionMessages;
import backend.models.response.ExceptionResponse;
import backend.models.response.Response;
import backend.services.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * get location by id tests
 *
 * @author Piotr Kuglin
 */
@RunWith(SpringRunner.class)
@WebMvcTest(LocationService.class)
@ActiveProfiles(value = ProfileTypes.TEST_PROFILE)
public class GetLocationByIdTest {

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

    @Test
    public void should_occur_not_found_exception_caused_by_invalid_location_id() throws Exception {
        Mockito.when(locationRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        ExceptionResponse bodyOfResponse = new ExceptionResponse(Response.MessageType.WARNING, ExceptionMessages.LOCATION_NOT_FOUND, ExceptionDescriptions.NOT_FOUND, HttpStatus.NOT_FOUND);

        mockMvc.perform(get(ContextPaths.LOCATION_MAIN_CONTEXT + "/5d87c214857aba0001625f7a").header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isNotFound()).andExpect(content().json(objectMapper.writeValueAsString(bodyOfResponse)));
    }

    @Test
    public void should_return_details_of_location() throws Exception {
        Mockito.when(locationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(new LocationEntity()));

        mockMvc.perform(get(ContextPaths.LOCATION_MAIN_CONTEXT + "/5d87c214857aba0001625f7a").header(HttpHeaders.AUTHORIZATION, "Bearer " + exampleToken))
                .andExpect(status().isOk());
    }
}
