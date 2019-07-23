package backend.endpoints.controllers;

import backend.endpoints.ContextPaths;
import backend.endpoints.responses.location.CreateLocationResponse;
import backend.endpoints.responses.location.EditLocationResponse;
import backend.endpoints.responses.location.ExistingLocationResponse;
import backend.endpoints.responses.location.UniqueCodeResponse;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocationController {

    @PostMapping(ContextPaths.LOCATION_CREATE)
    public ResponseEntity<?> createLocation(/*TODO LocationClass as argument*/){
        CreateLocationResponse response = new CreateLocationResponse();

        //TODO add location to database
        return ResponseEntity.status(HttpStatus.CREATED).body(new Resource<>(response));
    }

    @PutMapping(ContextPaths.LOCATION_EDIT)
    public ResponseEntity<?> editLocation(/*TODO ID of Location, new instance of location */){
        EditLocationResponse response = new EditLocationResponse();

        //TODO if ID is correct edit location, return ACCEPTED, else BAD REQUEST
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Resource<>(response));
    }

    @GetMapping(ContextPaths.LOCATION_CODE)
    public ResponseEntity<?> getUniqueCode(/*TODO name*/){
        UniqueCodeResponse response = new UniqueCodeResponse();

        //TODO if name is not correct BAD REQUEST, else OK
        return ResponseEntity.status(HttpStatus.OK).body(new Resource<>(response));
    }

    @PostMapping(ContextPaths.LOCATION_ADD)
    public ResponseEntity<?> addExisitingLocation(/*TODO id of location*/){
        ExistingLocationResponse response = new ExistingLocationResponse();

        //TODO if location id existing in database, add location to user account and OK, else BAD REQUEST
        return ResponseEntity.status(HttpStatus.OK).body(new Resource<>(response));
    }

}
