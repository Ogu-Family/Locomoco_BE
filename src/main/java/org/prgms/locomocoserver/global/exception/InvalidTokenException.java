package org.prgms.locomocoserver.global.exception;

public class InvalidTokenException extends RuntimeException {
    private ErrorCode errorCode;
    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
