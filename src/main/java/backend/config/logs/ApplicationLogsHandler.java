package backend.config.logs;

import backend.models.response.ExceptionResponse;
import backend.models.response.user.UserCreationResponseDto;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;


@Aspect
@Configuration
public class ApplicationLogsHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Pointcut("execution(* backend.exceptions.RestControllerExceptionHandler.*(..))")
    public void exceptionPointcut() {
        // Do nothing because of it's pointcut.
    }

    @AfterReturning(pointcut = "exceptionPointcut()", returning = "returnedValue")
    public void doExceptionLogs(Object returnedValue) {
        ResponseEntity responseEntity = (ResponseEntity) returnedValue;
        ExceptionResponse exceptionResponse = (ExceptionResponse) responseEntity.getBody();

        assert exceptionResponse != null;

        if (exceptionResponse.getType().equals(ExceptionResponse.ExceptionType.WARNING.toString()))
            log.warn("{} - {}", exceptionResponse.getStatus(), exceptionResponse.getMessage());
        else
            log.error("{} - {}", exceptionResponse.getStatus(), exceptionResponse.getMessage());
    }


    @Pointcut("execution(* backend.services.UserService.createUser(..))")
    public void createUserPointcut() {
        // Do nothing because of it's pointcut.
    }

    @AfterReturning(pointcut = "createUserPointcut()", returning = "returnedValue")
    public void doCreateUserLogs(Object returnedValue) {
        ResponseEntity responseEntity = (ResponseEntity) returnedValue;
        UserCreationResponseDto userCreationResponse = (UserCreationResponseDto) responseEntity.getBody();

        assert userCreationResponse != null;
        String username = userCreationResponse.getUser().getUsername();

        log.info("user created: {}", username);
    }
}
