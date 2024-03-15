package org.prgms.locomocoserver.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorType {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자가 존재하지 않습니다."),
    PROVIDER_TYPE_ERROR(HttpStatus.UNAUTHORIZED, "해당 provider가 존재하지 않습니다."),
    BIRTH_TYPE_ERROR(HttpStatus.BAD_REQUEST, "생년월일이 형식에 맞지 않습니다."),
    EMAIL_TYPE_ERROR(HttpStatus.BAD_REQUEST, "이메일이 형식에 맞지 않습니다."),
    TEMPERATURE_TYPE_ERROR(HttpStatus.BAD_REQUEST, "온도는 0도 이상 100도 이하의 값이어야 합니다.")
    ;

    private final HttpStatus status;
    private final String message;

    UserErrorType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
