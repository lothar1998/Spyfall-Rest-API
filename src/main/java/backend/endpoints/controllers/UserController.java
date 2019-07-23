package backend.endpoints.controllers;

import backend.databases.entities.UserEntity;
import backend.databases.repositories.UserRepository;
import backend.endpoints.ContextPaths;
import backend.endpoints.JWTParser;
import backend.endpoints.requests.CreateUserRequest;
import backend.endpoints.responses.Response;
import backend.endpoints.responses.user.ChangePasswordResponse;
import backend.endpoints.responses.user.CreateUserResponse;
import backend.endpoints.responses.user.LoginResponse;
import backend.endpoints.responses.user.LogoutResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    @PostMapping(ContextPaths.USER_CREATE)
    public ResponseEntity<?> createClient(@RequestBody CreateUserRequest userToCreate){
        Map<String,Object> responseContent = new HashMap<>();
        CreateUserResponse response = new CreateUserResponse();

        if(userRepository.findUserByUsername(userToCreate.getUsername())!=null){
            responseContent.put("message", "user already exists");
            response.setStatus(Response.Status.error).setContent(responseContent);

            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }



        UserEntity createdUser = userRepository.save(new UserEntity(userToCreate.getUsername(),passwordEncoder.encode(userToCreate.getPassword()),userToCreate.getEmail(),userToCreate.getAuthority()));

        if(createdUser==null){

            responseContent.put("message","user has not been created because of internal server error");
            response.setStatus(Response.Status.error).setContent(responseContent);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }


        responseContent.put("message","user has been created");
        response.setStatus(Response.Status.ok).setContent(responseContent);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(ContextPaths.USER_LOGIN)
    public ResponseEntity<?> loginClient(/*TODO Login credentials as argument*/){
        LoginResponse response = new LoginResponse();

        userRepository.findAll().forEach(System.out::println);

        //TODO try login and password, if correct ACCEPTED, else FORBIDDEN
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Resource<>(response));
    }

    @PostMapping(ContextPaths.USER_LOGOUT)
    public ResponseEntity<?> logoutClient(/*TODO logout class as argument*/){
        LogoutResponse response = new LogoutResponse();

        //TODO try logout, if ok OK, else INTERNAL SERVER ERROR
        return ResponseEntity.ok().body(new Resource<>(response));
    }

    @PostMapping(ContextPaths.USER_CHANGE_PASSWORD)
    public ResponseEntity<?> changePassword(@RequestHeader Map<String, String> headers/*TODO changepassword class as argument*/){

        String header = headers.get("authorization").split(" ")[1];


        System.out.println(JWTParser.getContent(header).get("user_name"));

        ChangePasswordResponse response = new ChangePasswordResponse();

        //TODO try old password, if correct, replace into new_password and HTTP ACCEPTED, else FORBIDDEN
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Resource<>(response));
    }
}
