package org.prgms.locomocoserver.global.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private ErrorCode errorCode;

    public AuthException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AuthException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
