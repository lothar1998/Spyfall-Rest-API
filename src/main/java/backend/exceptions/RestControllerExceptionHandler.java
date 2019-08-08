package backend.exceptions;

import backend.models.response.ExceptionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public final class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BadCredentialsException.class, UserHasNotBeenCreatedException.class})
    private ResponseEntity handleBadRequestException(Exception exception, WebRequest request) {
        ExceptionResponse bodyOfResponse = new ExceptionResponse(ExceptionResponse.ExceptionType.ERROR, exception.getMessage(), ExceptionDescriptions.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = DatabaseException.class)
    private ResponseEntity handleInternalServerErrorException(Exception exception, WebRequest request) {
        ExceptionResponse bodyOfResponse = new ExceptionResponse(ExceptionResponse.ExceptionType.FATAL_ERROR, exception.getMessage(), ExceptionDescriptions.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
