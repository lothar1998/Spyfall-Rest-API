package backend.endpoints;

import backend.endpoints.responses.client.ChangePasswordResponse;
import backend.endpoints.responses.client.CreateClientResponse;
import backend.endpoints.responses.client.LoginResponse;
import backend.endpoints.responses.client.LogoutResponse;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class ClientController {

    @PostMapping("/create")
    public ResponseEntity<?> createClient(/*TODO Client class as argument*/){
        CreateClientResponse response = new CreateClientResponse();

        //TODO try create new client, if created CREATED, else INTERNAL SERVER ERROR`
        return ResponseEntity.status(HttpStatus.CREATED).body(new Resource<>(response));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginClient(/*TODO Login credentials as argument*/){
        LoginResponse response = new LoginResponse();

        //TODO try login and password, if correct ACCEPTED, else FORBIDDEN
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Resource<>(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutClient(/*TODO logout class as argument*/){
        LogoutResponse response = new LogoutResponse();

        //TODO try logout, if ok OK, else INTERNAL SERVER ERROR
        return ResponseEntity.ok().body(new Resource<>(response));
    }

    @PostMapping("/password")
    public ResponseEntity<?> changePassword(/*TODO changepassword class as argument*/){
        ChangePasswordResponse response = new ChangePasswordResponse();

        //TODO try old password, if correct, replace into new_password and HTTP ACCEPTED, else FORBIDDEN
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Resource<>(response));
    }
}
