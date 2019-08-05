package backend.endpoints.controllers;

import backend.config.oauth2.UsersRoles;
import backend.config.startup.StartupConfig;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.UserRepository;
import backend.endpoints.ContextPaths;
import backend.endpoints.JWTParser;
import backend.endpoints.requests.user.UserChangePasswordRequest;
import backend.endpoints.requests.user.UserCreationRequest;
import backend.endpoints.responses.Response;
import backend.endpoints.responses.user.change_password.UserChangePasswordMessages;
import backend.endpoints.responses.user.change_password.UserChangePasswordResponse;
import backend.endpoints.responses.user.creation.UserCreationMessages;
import backend.endpoints.responses.user.creation.UserCreationResponse;
import backend.endpoints.responses.user.signed_users_list.UserListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * implementation of REST controller for users queries
 *
 * @author Piotr Kuglin
 */
@RestController
@RequestMapping(ContextPaths.USER_MAIN_CONTEXT)
public class UserController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * create user with given request
     * @param user user's credentials
     * @param errors occurs errors
     * @return query response
     */
    @Secured({UsersRoles.ADMIN, UsersRoles.USER})
    @PostMapping(ContextPaths.USER_CREATE)
    public ResponseEntity createUser(@Valid @RequestBody UserCreationRequest user, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserCreationResponse(Response.MessageType.ERROR, UserCreationMessages.BAD_CREDENTIALS, null));
        }

        if (userRepository.findUserByUsername(user.getUsername()) != null)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new UserCreationResponse(Response.MessageType.ERROR, UserCreationMessages.USER_ALREADY_EXISTS, null));

        UserEntity savedUser = userRepository.save(new UserEntity(user.getUsername(), passwordEncoder.encode(user.getPassword()), user.getEmail(), UsersRoles.USER));

        if (savedUser == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserCreationResponse(Response.MessageType.ERROR, UserCreationMessages.DATABASE_ERROR, null));

        savedUser.setPassword(StartupConfig.HASHED_PASSWORD_REPLACEMENT);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserCreationResponse(Response.MessageType.MESSAGE, UserCreationMessages.USER_HAS_BEEN_CREATED, savedUser));
    }

    /**
     * change user's password
     * @param request user's old and new password
     * @param errors occurs errors
     * @param header authorization header - JWT Token
     * @return query response
     */
    @Secured({UsersRoles.ADMIN, UsersRoles.USER})
    @PostMapping(ContextPaths.USER_CHANGE_PASSWORD)
    public ResponseEntity changePassword(@Valid @RequestBody UserChangePasswordRequest request, Errors errors, @RequestHeader Map<String, String> header) {
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserChangePasswordResponse(Response.MessageType.ERROR, UserChangePasswordMessages.BAD_CREDENTIALS));

        String token = header.get("authorization").split(" ")[1];

        String username = JWTParser.getContent(token).get("user_name").toString();

        UserEntity foundUser = userRepository.findUserByUsername(username);

        if (foundUser == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new UserChangePasswordResponse(Response.MessageType.ERROR, UserChangePasswordMessages.BAD_CREDENTIALS));

        if (!passwordEncoder.matches(request.getOldPassword(), foundUser.getPassword()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserChangePasswordResponse(Response.MessageType.ERROR, UserChangePasswordMessages.BAD_CREDENTIALS));

        UserEntity savedUser = userRepository.save(foundUser.setPassword(passwordEncoder.encode(request.getNewPassword())));

        if (savedUser == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserChangePasswordResponse(Response.MessageType.ERROR, UserChangePasswordMessages.DATABASE_ERROR));

        return ResponseEntity.status(HttpStatus.OK).body(new UserChangePasswordResponse(Response.MessageType.MESSAGE, UserChangePasswordMessages.PASSWORD_HAS_BEEN_CHANGED));
    }

    /**
     * show all signed user lists
     * @return response for query
     */
    @Secured(UsersRoles.ADMIN)
    @GetMapping(ContextPaths.USER_GET_ALL_USERS)
    public ResponseEntity showAllSignedUsers() {
        List<UserEntity> users = new ArrayList<>();

        Iterable<UserEntity> userList = userRepository.findAll();

        if (userList == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserListResponse(Response.MessageType.ERROR, null));

        userList.forEach(user -> {
            user.setPassword(StartupConfig.HASHED_PASSWORD_REPLACEMENT);
            users.add(user);
        });

        return ResponseEntity.status(HttpStatus.OK).body(new UserListResponse(Response.MessageType.STATUS, users));
    }
}
