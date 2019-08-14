package backend.services;

import backend.config.ContextPaths;
import backend.config.oauth2.UsersRoles;
import backend.config.startup.StartupConfig;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.UserRepository;
import backend.exceptions.DatabaseException;
import backend.exceptions.ExceptionMessages;
import backend.models.request.user.PasswordChangeDto;
import backend.models.request.user.UserCreationDto;
import backend.models.response.Response;
import backend.models.response.ResponseMessages;
import backend.models.response.user.PasswordChangeResponseDto;
import backend.models.response.user.UserCreationResponseDto;
import backend.models.response.user.UserListResponseDto;
import backend.parsers.JWTParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


/**
 * implementation of REST controller for users queries
 *
 * @author Piotr Kuglin
 */
@RestController
@RequestMapping(ContextPaths.USER_MAIN_CONTEXT)
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
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
    public ResponseEntity createUser(@Valid @RequestBody UserCreationDto user, Errors errors) {
        if (errors.hasErrors())
            throw new BadCredentialsException(ExceptionMessages.VALIDATION_ERROR);

        if (userRepository.findUserByUsername(user.getUsername()) != null)
            throw new BadCredentialsException(ExceptionMessages.USER_ALREADY_EXISTS);

        UserEntity userToSave = new UserEntity(user.getUsername(), passwordEncoder.encode(user.getPassword()), user.getEmail(), UsersRoles.USER);

        UserEntity savedUser = userRepository.save(userToSave);

        userToSave.setId(savedUser.getId());
        userToSave.setAuthority(savedUser.getAuthority());

        if (!savedUser.equals(userToSave))
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        savedUser.setPassword(StartupConfig.HASHED_PASSWORD_REPLACEMENT);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserCreationResponseDto(Response.MessageType.INFO, ResponseMessages.USER_HAS_BEEN_CREATED, savedUser));
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
    public ResponseEntity changePassword(@Valid @RequestBody PasswordChangeDto request, Errors errors, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header) {
        if (errors.hasErrors())
            throw new BadCredentialsException(ExceptionMessages.VALIDATION_ERROR);

        String token = header.split(" ")[1];
        String username = JWTParser.getContent(token).get("user_name").toString();

        UserEntity foundUser = userRepository.findUserByUsername(username);

        if (foundUser == null)
            throw new BadCredentialsException(ExceptionMessages.VALIDATION_ERROR);

        if (!passwordEncoder.matches(request.getOldPassword(), foundUser.getPassword()))
            throw new BadCredentialsException(ExceptionMessages.VALIDATION_ERROR);

        foundUser.setPassword(passwordEncoder.encode(request.getNewPassword()));

        UserEntity savedUser = userRepository.save(foundUser);

        if (!savedUser.equals(foundUser))
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        return ResponseEntity.status(HttpStatus.OK).body(new PasswordChangeResponseDto(Response.MessageType.INFO, ResponseMessages.PASSWORD_HAS_BEEN_CHANGED));
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

        if (!userList.iterator().hasNext())
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        userList.forEach(user -> {
            user.setPassword(StartupConfig.HASHED_PASSWORD_REPLACEMENT);
            users.add(user);
        });

        return ResponseEntity.status(HttpStatus.OK).body(new UserListResponseDto(Response.MessageType.STATUS, ResponseMessages.LIST_OF_USERS_SHOWN, users));
    }
}
