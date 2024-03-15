package org.prgms.locomocoserver.global.exception;

public class ExpiredTokenException extends RuntimeException {
    ErrorCode errorCode;
    public ExpiredTokenException(ErrorCode code) {
        super(code.getMessage());
        this.errorCode = code;
    }
}
