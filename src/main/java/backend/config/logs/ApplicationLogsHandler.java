package backend.config.logs;

import backend.config.ProfileTypes;
import backend.models.response.Response;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Aspect
@Configuration
@Profile(ProfileTypes.PRODUCTION_PROFILE)
public class ApplicationLogsHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Pointcut(value = "execution(org.springframework.http.ResponseEntity *.*(..)) && args(..,request) && @annotation(backend.config.logs.WarningLog)")
    public void exceptionWarningPointcut(WebRequest request) {
        // Do nothing because of it's pointcut.
    }

    @Around(value = "exceptionWarningPointcut(request)", argNames = "joinPoint, request")
    public Object doExceptionWarningLogs(ProceedingJoinPoint joinPoint, WebRequest request) throws Throwable {
        ResponseEntity returnedValue = (ResponseEntity) joinPoint.proceed();

        String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
        String method = ((ServletWebRequest) request).getRequest().getMethod();

        Object body = returnedValue.getBody();

        if (body == null)
            return joinPoint.proceed();

        String message;

        if (body instanceof Response)
            message = ((Response) body).getMessage();
        else
            message = body.toString();

        log.warn("{}: {}  ({}) ---> cause: {}", method, uri, request.getRemoteUser(), message);
        return joinPoint.proceed();
    }

    @Pointcut(value = "execution(* backend.exceptions.RestControllerExceptionHandler.*(..)) && args(exception, ..) && @annotation(backend.config.logs.ErrorLog)", argNames = "exception")
    public void exceptionErrorPointcut(Exception exception) {
        // Do nothing because of it's pointcut.
    }

    @Before(value = "exceptionErrorPointcut(exception)", argNames = "exception")
    public void doExceptionErrorLogs(Exception exception) {
        log.error("", exception);
    }
}
