package backend.exceptions;

import backend.config.logs.ErrorLog;
import backend.config.logs.WarningLog;
import backend.exceptions.game.*;
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

import javax.validation.ValidationException;
/**
 * controller exceptions handlers
 *
 * @author Piotr Kuglin
 */
@RestControllerAdvice
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity handleNotFoundException(Exception exception, WebRequest request) {
        ExceptionResponse bodyOfResponse = new ExceptionResponse(Response.MessageType.WARNING, exception.getMessage(), ExceptionDescriptions.NOT_FOUND, HttpStatus.NOT_FOUND);
        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @WarningLog
    @ExceptionHandler(value = {BadCredentialsException.class, ValidationException.class})
    public ResponseEntity handleBadRequestException(Exception exception, WebRequest request) {
        ExceptionResponse bodyOfResponse = new ExceptionResponse(Response.MessageType.WARNING, exception.getMessage(), ExceptionDescriptions.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @WarningLog
    @ExceptionHandler(value = PermissionDeniedException.class)
    public ResponseEntity handlePermissionDeniedException(Exception exception, WebRequest request) {
        ExceptionResponse bodyOfResponse = new ExceptionResponse(Response.MessageType.ERROR, exception.getMessage(), ExceptionDescriptions.PERMISSION_DENIED, HttpStatus.FORBIDDEN);
        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ErrorLog
    @ExceptionHandler(value = DatabaseException.class)
    public ResponseEntity handleInternalServerErrorException(Exception exception, WebRequest request) {
        ExceptionResponse bodyOfResponse = new ExceptionResponse(Response.MessageType.ERROR, exception.getMessage(), ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @WarningLog
    @ExceptionHandler(value = GameActionForbiddenException.class)
    public ResponseEntity handleGameActionForbiddenException(Exception exception, WebRequest request) {
        ExceptionResponse bodyOfResponse = new ExceptionResponse(Response.MessageType.WARNING, exception.getMessage(), ExceptionDescriptions.PERMISSION_DENIED, HttpStatus.FORBIDDEN);
        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ErrorLog
    @ExceptionHandler(value = TooManyPlayersException.class)
    public ResponseEntity handleTooManyPlayers(Exception exception, WebRequest request) {
        ExceptionResponse bodyOfResponse = new ExceptionResponse(Response.MessageType.ERROR, exception.getMessage(), ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.FORBIDDEN);
        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }
}
