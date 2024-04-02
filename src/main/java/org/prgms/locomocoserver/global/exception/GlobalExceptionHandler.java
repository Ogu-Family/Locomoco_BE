package org.prgms.locomocoserver.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.image.exception.ImageException;
import org.prgms.locomocoserver.mogakkos.exception.MogakkoException;
import org.prgms.locomocoserver.review.exception.ReviewException;
import org.prgms.locomocoserver.user.exception.UserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<Object> handleUserException(UserException ex) {
        return ResponseEntity.status(ex.getErrorType().getStatus()).body(ex.getErrorType().getMessage());
    }

    @ExceptionHandler(ImageException.class)
    public ResponseEntity<Object> handleImageException(ImageException ex) {
        return ResponseEntity.status(ex.getErrorType().getStatus()).body(ex.getErrorType().getMessage());
    }

    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<Object> handleReviewException(ReviewException ex) {
        return ResponseEntity.status(ex.getErrorType().getStatus()).body(ex.getErrorType().getMessage());
    }

    @ExceptionHandler(MogakkoException.class)
    public ResponseEntity<?> handleMogakkoException(MogakkoException ex) {
        log.error(ex.getErrorCode().getMessage());
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(ex.getErrorCode().getMessage());
    }
}
