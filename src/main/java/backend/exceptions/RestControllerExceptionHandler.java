package backend.exceptions;

import backend.config.logs.ErrorLog;
import backend.config.logs.WarningLog;
import backend.models.response.ExceptionResponse;
import backend.models.response.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * controller exceptions handlers
 *
 * @author Piotr Kuglin
 */
@RestControllerAdvice
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @WarningLog
    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity handleBadRequestException(Exception exception, WebRequest request) {
        ExceptionResponse bodyOfResponse = new ExceptionResponse(Response.MessageType.WARNING, exception.getMessage(), ExceptionDescriptions.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ErrorLog
    @ExceptionHandler(value = DatabaseException.class)
    public ResponseEntity handleInternalServerErrorException(Exception exception, WebRequest request) {
        ExceptionResponse bodyOfResponse = new ExceptionResponse(Response.MessageType.ERROR, exception.getMessage(), ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
