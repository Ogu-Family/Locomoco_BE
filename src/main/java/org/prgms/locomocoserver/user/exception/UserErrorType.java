package org.prgms.locomocoserver.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorType {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자가 존재하지 않습니다.");

    private final HttpStatus status;
    private final String message;

    UserErrorType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
