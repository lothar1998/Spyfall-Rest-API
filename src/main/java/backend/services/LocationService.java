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
import backend.exceptions.NotFoundException;
import backend.exceptions.PermissionDeniedException;
import backend.models.request.location.LocationCreationDto;
import backend.models.response.Response;
import backend.models.response.ResponseMessages;
import backend.models.response.location.LocationByIdResponseDto;
import backend.models.response.location.LocationCreationResponseDto;
import backend.models.response.location.LocationDeletionResponseDto;
import backend.models.response.location.LocationsListByUsernameResponseDto;
import backend.parsers.UsernameParser;
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
import java.util.Optional;

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

        UserEntity owner = userRepository.findUserByUsername(UsernameParser.getUsername(header));

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

    /**
     * get all locations assigned to user by username
     *
     * @param header authorization bearer token
     * @return list of assigned locations to user
     * @throws DatabaseException occur when owner is not found in database
     */
    @Secured({UsersRoles.ADMIN, UsersRoles.USER})
    @GetMapping(ContextPaths.LOCATION_GET_ALL_BY_USERNAME)
    public ResponseEntity getAllLocationsByUsername(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String header) throws DatabaseException {
        String username = UsernameParser.getUsername(header);

        UserEntity user = userRepository.findUserByUsername(username);

        if (user == null)
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        List<LocationEntity> locationList = locationRepository.findByOwner(user);

        return ResponseEntity.status(HttpStatus.OK).body(new LocationsListByUsernameResponseDto(Response.MessageType.INFO, ResponseMessages.LIST_OF_LOCATIONS_BY_USERNAME, locationList));
    }

    /**
     * get location by location's ID
     *
     * @param id ID of location
     * @return locations details
     */
    @Secured({UsersRoles.ADMIN, UsersRoles.USER})
    @GetMapping("/{id}")
    public ResponseEntity getLocationById(@PathVariable String id) throws NotFoundException {
        Optional<LocationEntity> locationOptional = locationRepository.findById(id);

        if (!locationOptional.isPresent())
            throw new NotFoundException(ExceptionMessages.LOCATION_NOT_FOUND);

        LocationEntity location = locationOptional.get();

        return ResponseEntity.status(HttpStatus.OK).body(new LocationByIdResponseDto(Response.MessageType.INFO, ResponseMessages.LOCATION_BY_ID, location));
    }

    /**
     * delete location with corresponding roles by ID
     *
     * @param id     ID of location
     * @param header authorization JWT Token
     * @return info - successfully deleted location
     * @throws DatabaseException         occurs if there is no such user in database
     * @throws NotFoundException         occurs if location id is not valid
     * @throws PermissionDeniedException occurs if user has no permission to delete this location
     */
    @Secured({UsersRoles.ADMIN, UsersRoles.USER})
    @DeleteMapping("/{id}")
    public ResponseEntity deleteLocationById(@PathVariable String id, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String header) throws DatabaseException, NotFoundException, PermissionDeniedException {
        String username = UsernameParser.getUsername(header);

        Optional<LocationEntity> locationOptional = locationRepository.findById(id);

        if (!locationOptional.isPresent())
            throw new NotFoundException(ExceptionMessages.LOCATION_NOT_FOUND);

        LocationEntity location = locationOptional.get();

        UserEntity user = userRepository.findUserByUsername(username);

        if (user == null)
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        if (!location.getOwner().equals(user))
            throw new PermissionDeniedException(ExceptionMessages.DELETION_VALIDATION_ERROR);

        roleRepository.deleteAll(location.getRoles());
        locationRepository.delete(location);

        return ResponseEntity.status(HttpStatus.OK).body(new LocationDeletionResponseDto(Response.MessageType.INFO, ResponseMessages.LOCATION_DELETION));
    }
}
