package backend.services;

import backend.config.ContextPaths;
import backend.databases.entities.RoleEntity;
import backend.databases.repositories.RoleRepository;
import backend.exceptions.DatabaseException;
import backend.exceptions.ExceptionMessages;
import backend.models.request.role.RoleCreationDto;
import backend.models.response.Response;
import backend.models.response.ResponseMessages;
import backend.models.response.role.RoleCreationResponseDto;
import backend.models.response.role.RoleListResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


/**
 * implementation of REST controller for users queries
 *
 * @author Kamil Kaliś
 */
@RestController
@RequestMapping(ContextPaths.ROLE_MAIN_CONTEXT)
public class RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    /**
     * create role with given request
     * @param role role's contents
     * @param errors occurs errors
     * @return query response
     */
    @PostMapping(ContextPaths.ROLE_CREATE)
    public ResponseEntity createRole(@Valid @RequestBody RoleCreationDto role, Errors errors){
        if (errors.hasErrors())
            throw new BadCredentialsException(ExceptionMessages.VALIDATION_ERROR);

        if(roleRepository.findByName(role.getRoleName()) != null)
            throw new BadCredentialsException(ExceptionMessages.ROLE_ALREADY_EXISTS);


        RoleEntity roleToSave = new RoleEntity(role.getRoleName(),role.getRoleDescription());

        RoleEntity savedRole = roleRepository.save(roleToSave);

        roleToSave.setId(savedRole.getId());

        if(!savedRole.equals(roleToSave))
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        return  ResponseEntity.status(HttpStatus.CREATED).body(new RoleCreationResponseDto(Response.MessageType.INFO, ResponseMessages.ROLE_HAS_BEEN_CREATED,savedRole));
    }


    /**
     * show all existing roles lists
     * @return response for query
     */
    @GetMapping(ContextPaths.ROLE_GET_ALL_ROLES)
    public ResponseEntity getAllRoles(){
        List<RoleEntity> roles = new ArrayList<>();

        Iterable<RoleEntity> roleList = roleRepository.findAll();

        if(!roleList.iterator().hasNext())
            throw new DatabaseException(ExceptionMessages.DATABASE_ERROR);

        roleList.forEach(roles::add);

        return ResponseEntity.status(HttpStatus.OK).body(new RoleListResponseDto(Response.MessageType.STATUS, ResponseMessages.LIST_OF_ROLES_SHOWN,roles));
    }

}
