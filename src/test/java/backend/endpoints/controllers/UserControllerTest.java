package backend.endpoints.controllers;

import backend.databases.entities.UserEntity;
import backend.databases.repositories.UserRepository;
import backend.endpoints.requests.CreateUserRequest;
import backend.endpoints.responses.Response;
import backend.endpoints.responses.user.CreateUserResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserController userController = new UserController(userRepository,passwordEncoder);

    @Test
    public void should_create_user() {
        //given
        CreateUserRequest userToCreate = new CreateUserRequest("lorema","ipsumdolor","sit@amenc.pl","USER");

        UserEntity createdUser = new UserEntity(userToCreate.getUsername(),passwordEncoder.encode(userToCreate.getPassword()),userToCreate.getEmail(),userToCreate.getAuthority());
        createdUser.setId(1L);

        Map<String, Object> content = new HashMap<>();
        content.put("message","user has been created");
        ResponseEntity<?> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserResponse(Response.Status.ok, content));

        //when
        Mockito.when(passwordEncoder.encode(userToCreate.getPassword())).thenReturn("encoded_pass");
        Mockito.when(userRepository.save(new UserEntity(userToCreate.getUsername(),passwordEncoder.encode(userToCreate.getPassword()),userToCreate.getEmail(),userToCreate.getAuthority()))).thenReturn(createdUser);

        //then
        final ResponseEntity<?> response = userController.createClient(userToCreate);


        assertEquals(expectedResponse.getStatusCode(),response.getStatusCode());
    }

    @Test
    public void should_handle_database_error(){
        //given
        CreateUserRequest userToCreate = new CreateUserRequest("lorema","ipsumdolor","sit@amenc.pl","USER");

        UserEntity createdUser = new UserEntity(userToCreate.getUsername(),passwordEncoder.encode(userToCreate.getPassword()),userToCreate.getEmail(),userToCreate.getAuthority());
        createdUser.setId(1L);

        Map<String, Object> content = new HashMap<>();
        content.put("message","user has not been created because of internal server error");
        ResponseEntity<?> expectedResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CreateUserResponse(Response.Status.error, content));

        //when
        Mockito.when(passwordEncoder.encode(userToCreate.getPassword())).thenReturn("encoded_pass");
        Mockito.when(userRepository.save(new UserEntity(userToCreate.getUsername(),passwordEncoder.encode(userToCreate.getPassword()),userToCreate.getEmail(),userToCreate.getAuthority()))).thenReturn(null);

        //then
        final ResponseEntity<?> response = userController.createClient(userToCreate);


        assertEquals(expectedResponse.getStatusCode(),response.getStatusCode());
    }

    @Test
    public void should_not_create_user_with_existing_username(){
        //given
        CreateUserRequest userToCreate = new CreateUserRequest("lorema","ipsumdolor","sit@amenc.pl","USER");

        UserEntity createdUser = new UserEntity(userToCreate.getUsername(),passwordEncoder.encode(userToCreate.getPassword()),userToCreate.getEmail(),userToCreate.getAuthority());
        createdUser.setId(1L);

        Map<String, Object> content = new HashMap<>();
        content.put("message","user already exists");
        ResponseEntity<?> expectedResponse = ResponseEntity.status(HttpStatus.CONFLICT).body(new CreateUserResponse(Response.Status.error, content));

        //when
        Mockito.when(userRepository.findUserByUsername(userToCreate.getUsername())).thenReturn(createdUser);

        //then
        final ResponseEntity<?> response = userController.createClient(userToCreate);


        assertEquals(expectedResponse.getStatusCode(),response.getStatusCode());
    }


}