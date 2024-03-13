package org.prgms.locomocoserver.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    ACCESSTOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,1401, "Access Token Expired"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST,400, "Invalid Token");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, int code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
