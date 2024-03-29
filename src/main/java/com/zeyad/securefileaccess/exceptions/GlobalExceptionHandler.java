package com.zeyad.securefileaccess.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    protected ResponseEntity<?> handleConflict(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = {ForbiddenFileAccessException.class})
    protected ResponseEntity<?> handleConflict(ForbiddenFileAccessException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(value = {ResourceExistedException.class})
    protected ResponseEntity<?> handleConflict(ResourceExistedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(value = {NotAuthorizedException.class})
    protected ResponseEntity<?> handleConflict(NotAuthorizedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(value = {HttpStatusCodeException.class})
    protected ResponseEntity<?> handleConflict(HttpStatusCodeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.valueOf(ex.getRawStatusCode()));
    }
}
