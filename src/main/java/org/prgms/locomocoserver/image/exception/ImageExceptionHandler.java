package org.prgms.locomocoserver.image.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ImageExceptionHandler {

    @ExceptionHandler(ImageException.class)
    public ResponseEntity<Object> handleImageException(ImageException ex) {
        return ResponseEntity.status(ex.getErrorType().getStatus()).body(ex.getErrorType().getMessage());
    }
}
