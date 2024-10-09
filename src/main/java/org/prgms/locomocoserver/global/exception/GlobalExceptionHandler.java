package org.prgms.locomocoserver.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.global.common.SlackAlertService;
import org.prgms.locomocoserver.global.common.dto.SlackErrorAlertDto;
import org.prgms.locomocoserver.image.exception.ImageException;
import org.prgms.locomocoserver.mogakkos.exception.MogakkoException;
import org.prgms.locomocoserver.review.exception.ReviewException;
import org.prgms.locomocoserver.user.exception.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final SlackAlertService slackAlertService;

    @ExceptionHandler(UserException.class)
    public ResponseEntity<Object> handleUserException(UserException ex, HttpServletRequest request) {
        slackAlertService.sendAlertLog(
            new SlackErrorAlertDto(request.getRequestURL().toString(), request.getMethod(),
                ex.getErrorType().getMessage()));
        return ResponseEntity.status(ex.getErrorType().getStatus()).body(ex.getErrorType().getMessage());
    }

    @ExceptionHandler(ImageException.class)
    public ResponseEntity<Object> handleImageException(ImageException ex, HttpServletRequest request) {
        slackAlertService.sendAlertLog(
            new SlackErrorAlertDto(request.getRequestURL().toString(), request.getMethod(),
                ex.getErrorType().getMessage()));
        return ResponseEntity.status(ex.getErrorType().getStatus()).body(ex.getErrorType().getMessage());
    }

    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<Object> handleReviewException(ReviewException ex, HttpServletRequest request) {
        slackAlertService.sendAlertLog(
            new SlackErrorAlertDto(request.getRequestURL().toString(), request.getMethod(),
                ex.getErrorType().getMessage()));
        return ResponseEntity.status(ex.getErrorType().getStatus()).body(ex.getErrorType().getMessage());
    }

    @ExceptionHandler(MogakkoException.class)
    public ResponseEntity<?> handleMogakkoException(MogakkoException ex, HttpServletRequest request) {
        slackAlertService.sendAlertLog(
            new SlackErrorAlertDto(request.getRequestURL().toString(), request.getMethod(),
                ex.getErrorType().getMessage()));
        return ResponseEntity.status(ex.getErrorType().getHttpStatus()).body(ex.getErrorType().getMessage());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class,
        HttpMessageNotReadableException.class,
        MethodArgumentNotValidException.class
    })
    public ResponseEntity<?> handleClientException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ex.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnknownException(Exception ex, HttpServletRequest request) {
        slackAlertService.sendAlertLog(
            new SlackErrorAlertDto(request.getRequestURL().toString(), request.getMethod(),
                ex.getMessage()));
        log.error("알 수 없는 에러 발생: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
