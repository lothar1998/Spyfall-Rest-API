package backend.services;

import backend.config.ContextPaths;
import backend.config.oauth2.UsersRoles;
import backend.databases.entities.LocationEntity;
import backend.databases.entities.RoleEntity;
import backend.databases.entities.UserEntity;
import backend.databases.repositories.LocationRepository;
import backend.databases.repositories.RoleRepository;
import backend.databases.repositories.UserRepository;
import backend.exceptions.DatabaseException;
import backend.exceptions.ExceptionMessages;
import backend.models.request.location.LocationCreationDto;
import backend.models.response.Response;
import backend.models.response.ResponseMessages;
import backend.models.response.location.LocationCreationResponseDto;
import backend.parsers.UserNameParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * implementation of REST controller of location queries
 *
 * @author Piotr Kuglin
 */
@RestController
@RequestMapping(ContextPaths.LOCATION_MAIN_CONTEXT)
public class LocationService {

    private LocationRepository locationRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * create location with given request
     *
     * @param location location properties
     * @param errors   validation errors
     * @param header   JWT authorization bearer token
     * @return query response
     * @throws DatabaseException occurs when database returns incorrect responses
     */
    @Secured({UsersRoles.ADMIN, UsersRoles.USER})
    @PostMapping(ContextPaths.LOCATION_CREATE)
    public ResponseEntity createLocation(@Valid @RequestBody LocationCreationDto location, Errors errors, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header) throws DatabaseException {
        if (errors.hasErrors())
            throw new ValidationException(ExceptionMessages.VALIDATION_ERROR);

        UserEntity owner = userRepository.findUserByUsername(UserNameParser.getUsername(header));

        if (owner == null)
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        List<RoleEntity> savedRoles = new ArrayList<>();

        for (RoleEntity r : location.getRoles()) {
            r.setOwner(owner);
            savedRoles.add(roleRepository.save(r));
        }

        LocationEntity locationToSave = new LocationEntity(location.getName(), owner, location.getDescription(), savedRoles, Calendar.getInstance().getTime());

        LocationEntity savedLocation = locationRepository.save(locationToSave);

        locationToSave.setId(savedLocation.getId());

        if (!savedLocation.equals(locationToSave))
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        return ResponseEntity.status(HttpStatus.CREATED).body(new LocationCreationResponseDto(Response.MessageType.INFO, ResponseMessages.LOCATION_HAS_BEEN_CREATED, savedLocation));
    }

}
