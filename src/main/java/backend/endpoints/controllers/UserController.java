package backend.endpoints.controllers;

import backend.StartupConfig;
import backend.databases.entities.UserEntity;
import backend.databases.exception.NullIdException;
import backend.databases.repositories.UserRepository;
import backend.endpoints.ContextPaths;
import backend.endpoints.JWTParser;
import backend.endpoints.requests.ChangePasswordRequest;
import backend.endpoints.requests.CreateUserRequest;
import backend.endpoints.responses.Response;
import backend.endpoints.responses.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * implementation of REST controller for users queries
 *
 * @author Piotr Kuglin
 */
@RestController
public class UserController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    /**
     * endpoint which is used to create new user
     * @param userToCreate object of new user to creation
     * @return response of server for creation query
     */
    @PostMapping(ContextPaths.USER_CREATE)
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest userToCreate){
        Map<String,Object> responseContent = new LinkedHashMap<>();
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

    /**
     * endpoint which is used to change password of user
     * @param headers HTTP header with authorization param which contains Bearer Token
     * @param userCredentials old and new password of user
     * @return response with info about changing user's password
     */
    @PostMapping(ContextPaths.USER_CHANGE_PASSWORD)
    public ResponseEntity<?> changePassword(@RequestHeader Map<String, String> headers, @RequestBody ChangePasswordRequest userCredentials){
        ChangePasswordResponse response = new ChangePasswordResponse();
        Map<String, Object> responseContent = new LinkedHashMap<>();

        String header = headers.get("authorization").split(" ")[1];
        String username = JWTParser.getContent(header).get("user_name").toString();

        UserEntity foundUser = userRepository.findUserByUsername(username);

        if((foundUser == null) || (userCredentials.getPassword().length() < StartupConfig.passwordMinLength)){
            responseContent.put("message","bad credentials");
            response.setStatus(Response.Status.error).setContent(responseContent);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Resource<>(response));
        }
        else if(passwordEncoder.matches(userCredentials.getOldPassword(),foundUser.getPassword())){
            foundUser.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
            UserEntity savedUser = userRepository.save(foundUser);

            if(savedUser == null){
                responseContent.put("message","user has not been updated caused by internal server error");
                response.setStatus(Response.Status.error).setContent(responseContent);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Resource<>(response));
            }
            else if(savedUser.equals(foundUser)){
                responseContent.put("message","user has been updated");
                response.setStatus(Response.Status.ok).setContent(responseContent);
                return ResponseEntity.status(HttpStatus.OK).body(new Resource<>(response));
            }
            else{
                responseContent.put("message","internal server error");
                response.setStatus(Response.Status.error).setContent(responseContent);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Resource<>(response));
            }
        }
        else{
            responseContent.put("message","bad credentials");
            response.setStatus(Response.Status.error).setContent(responseContent);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Resource<>(response));
        }
    }

    /**
     * endpoint which is used to list all signed user
     * @return list of signed user as response for query
     */
    @GetMapping(ContextPaths.USER_GET_ALL_USERS)
    public ResponseEntity<?> getListOfCurrentSignedUsers(){
        SignedUserListResponse response = new SignedUserListResponse();

        Map<String, Object> responseContent = new LinkedHashMap<>();

        List<UserEntity> userFromRepository = new ArrayList<>();

        userRepository.findAll().forEach(userFromRepository::add);

        userFromRepository.forEach((user)->{
            Map<String, String> userDetails = new LinkedHashMap<>();

            userDetails.put("username",user.getUsername());
            userDetails.put("email", user.getEmail());
            userDetails.put("authority", user.getAuthority());

            if(user.getId()==null)
                throw new NullIdException(user.getUsername());

            responseContent.put(user.getId().toString(),userDetails);
        });

        response.setStatus(Response.Status.ok).setContent(responseContent);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
