package org.prgms.locomocoserver.user.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleUserException(UserException ex) {
        return ResponseEntity.status(ex.getErrorType().getStatus()).body(ex.getErrorType().getMessage());
    }
}
