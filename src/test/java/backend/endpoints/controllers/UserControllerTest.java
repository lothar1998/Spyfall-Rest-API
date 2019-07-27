package backend.endpoints.controllers;

import backend.databases.entities.UserEntity;
import backend.databases.exception.NullIdException;
import backend.databases.repositories.UserRepository;
import backend.endpoints.requests.ChangePasswordRequest;
import backend.endpoints.requests.CreateUserRequest;
import backend.endpoints.responses.Response;
import backend.endpoints.responses.user.CreateUserResponse;
import backend.oauth2.UsersRoles;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Mock
    UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private UserController userController;

    @Before
    public void setUp(){
        passwordEncoder = new BCryptPasswordEncoder();
        userController = new UserController(userRepository,passwordEncoder);
    }

    @Test
    public void createUser_should_create_user() {
        //given
        CreateUserRequest userToCreate = new CreateUserRequest("lorema","ipsumdolor","sit@amenc.pl","USER");

        UserEntity createdUser = new UserEntity(userToCreate.getUsername(),passwordEncoder.encode(userToCreate.getPassword()),userToCreate.getEmail(),userToCreate.getAuthority());
        createdUser.setId(1L);

        Map<String, Object> content = new LinkedHashMap<>();
        content.put("message","user has been created");
        ResponseEntity<?> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserResponse(Response.Status.ok, content));

        //when
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(createdUser);

        //then
        final ResponseEntity<?> response = userController.createUser(userToCreate);


        assertEquals(expectedResponse.getStatusCode(),response.getStatusCode());
    }

    @Test
    public void createUser_should_handle_database_error_during_user_creation(){
        //given
        CreateUserRequest userToCreate = new CreateUserRequest("lorema","ipsumdolor","sit@amenc.pl","USER");

        UserEntity createdUser = new UserEntity(userToCreate.getUsername(),passwordEncoder.encode(userToCreate.getPassword()),userToCreate.getEmail(),userToCreate.getAuthority());
        createdUser.setId(1L);

        Map<String, Object> content = new LinkedHashMap<>();
        content.put("message","user has not been created because of internal server error");
        ResponseEntity<?> expectedResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CreateUserResponse(Response.Status.error, content));

        //when
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(null);

        //then
        final ResponseEntity<?> response = userController.createUser(userToCreate);


        assertEquals(expectedResponse.getStatusCode(),response.getStatusCode());
    }

    @Test
    public void createUser_should_not_create_user_with_existing_username(){
        //given
        CreateUserRequest userToCreate = new CreateUserRequest("lorema","ipsumdolor","sit@amenc.pl","USER");

        UserEntity createdUser = new UserEntity(userToCreate.getUsername(),passwordEncoder.encode(userToCreate.getPassword()),userToCreate.getEmail(),userToCreate.getAuthority());
        createdUser.setId(1L);

        Map<String, Object> content = new LinkedHashMap<>();
        content.put("message","user already exists");
        ResponseEntity<?> expectedResponse = ResponseEntity.status(HttpStatus.CONFLICT).body(new CreateUserResponse(Response.Status.error, content));

        //when
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(createdUser);

        //then
        final ResponseEntity<?> response = userController.createUser(userToCreate);


        assertEquals(expectedResponse.getStatusCode(),response.getStatusCode());
    }

    @Test
    public void getListOfCurrentSignedUsers_should_response_list_of_all_users(){
        //given
        List<UserEntity> users = new ArrayList<>();
        users.add(new UserEntity("loremi", "impsumdolor", "sitamenc@gmail.com", "ROLE_ADMIN").setId(1L));
        users.add(new UserEntity("consectetur", "adipiscing", "elit@sed.com", "ROLE_USER").setId(2L));
        users.add(new UserEntity("eiusmod", "incididunt ", "incididunt@gmail.com", "ROLE_USER").setId(3L));

        //when
        Mockito.when(userRepository.findAll()).thenReturn(users);

        assertEquals(HttpStatus.OK, userController.getListOfCurrentSignedUsers().getStatusCode());
    }

    @Test(expected = NullIdException.class)
    public void getListOfCurrentSignedUsers_should_throw_null_pointer_exception_during_getting_list_of_all_users(){
        //given
        List<UserEntity> users = new ArrayList<>();
        users.add(new UserEntity("loremi", "impsumdolor", "sitamenc@gmail.com", "ROLE_ADMIN").setId(1L));
        users.add(new UserEntity("consectetur", "adipiscing", "elit@sed.com", "ROLE_USER"));
        users.add(new UserEntity("eiusmod", "incididunt ", "incididunt@gmail.com", "ROLE_USER").setId(3L));

        //when
        Mockito.when(userRepository.findAll()).thenReturn(users);

        userController.getListOfCurrentSignedUsers();
    }

    @Test
    public void changePassword_should_return_error_caused_by_incorrect_login() {
        //given
        String username = "janko1223";
        String oldPassword = "janko123";

        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjQwODgxNDQsInVzZXJfbmFtZSI6ImphbmtvMTIzIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6IjBiMDNjYTIwLTJmOTktNDFmYS04YTE5LTkwZTRkMWFmY2JjMSIsImNsaWVudF9pZCI6ImNsaWVudF9pZCIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.nUkGmSTp5oOXXyUEfdE5CQfkK38LuI-_UtzJoK_VFCw";

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword(oldPassword);
        request.setPassword("testpassword");

        Map<String,String> authorizationHeader = new LinkedHashMap<>();
        authorizationHeader.put("authorization","Bearer " + token);

        UserEntity user = new UserEntity(username,passwordEncoder.encode(oldPassword),"test@email.com", UsersRoles.USER).setId(1L);

        //when
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(null);
        //test

        assertEquals(HttpStatus.BAD_REQUEST, userController.changePassword(authorizationHeader,request).getStatusCode());
    }

    @Test
    public void changePassword_should_return_error_caused_by_incorrect_old_password() {
        //given
        String username = "janko123";
        String oldPassword = "janko123";

        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjQwODgxNDQsInVzZXJfbmFtZSI6ImphbmtvMTIzIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6IjBiMDNjYTIwLTJmOTktNDFmYS04YTE5LTkwZTRkMWFmY2JjMSIsImNsaWVudF9pZCI6ImNsaWVudF9pZCIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.nUkGmSTp5oOXXyUEfdE5CQfkK38LuI-_UtzJoK_VFCw";

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword(oldPassword);
        request.setPassword("testpassword");

        Map<String,String> authorizationHeader = new LinkedHashMap<>();
        authorizationHeader.put("authorization","Bearer " + token);

        UserEntity user = new UserEntity(username,passwordEncoder.encode(oldPassword + "xdd"),"test@email.com", UsersRoles.USER).setId(1L);

        //when
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(user);
        //test

        assertEquals(HttpStatus.BAD_REQUEST, userController.changePassword(authorizationHeader,request).getStatusCode());
    }

    @Test
    public void changePassword_should_return_error_caused_by_incorrect_new_password() {
        //given
        String username = "janko123";
        String oldPassword = "janko123";

        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjQwODgxNDQsInVzZXJfbmFtZSI6ImphbmtvMTIzIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6IjBiMDNjYTIwLTJmOTktNDFmYS04YTE5LTkwZTRkMWFmY2JjMSIsImNsaWVudF9pZCI6ImNsaWVudF9pZCIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.nUkGmSTp5oOXXyUEfdE5CQfkK38LuI-_UtzJoK_VFCw";

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword(oldPassword);
        request.setPassword("test");

        Map<String,String> authorizationHeader = new LinkedHashMap<>();
        authorizationHeader.put("authorization","Bearer " + token);

        UserEntity user = new UserEntity(username,passwordEncoder.encode(oldPassword),"test@email.com", UsersRoles.USER).setId(1L);

        //when
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(user);
        //test

        assertEquals(HttpStatus.BAD_REQUEST, userController.changePassword(authorizationHeader,request).getStatusCode());
    }

    @Test
    public void changePassword_should_return_ok_and_change_password() {
        //given
        String username = "janko123";
        String oldPassword = "janko123";

        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjQwODgxNDQsInVzZXJfbmFtZSI6ImphbmtvMTIzIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6IjBiMDNjYTIwLTJmOTktNDFmYS04YTE5LTkwZTRkMWFmY2JjMSIsImNsaWVudF9pZCI6ImNsaWVudF9pZCIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.nUkGmSTp5oOXXyUEfdE5CQfkK38LuI-_UtzJoK_VFCw";

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword(oldPassword);
        request.setPassword("testtest");

        Map<String,String> authorizationHeader = new LinkedHashMap<>();
        authorizationHeader.put("authorization","Bearer " + token);

        UserEntity user = new UserEntity(username,passwordEncoder.encode(oldPassword),"test@email.com", UsersRoles.USER).setId(1L);

        UserEntity foundUser = new UserEntity(user.getUsername(),user.getPassword(),user.getEmail(),user.getAuthority()).setId(user.getId());
        foundUser.setPassword(passwordEncoder.encode(request.getPassword()));

        //when
        Mockito.when(userRepository.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(userRepository.findUserByUsername(Mockito.anyString())).thenReturn(user);
        //test

        assertEquals(HttpStatus.OK, userController.changePassword(authorizationHeader,request).getStatusCode());
    }
}