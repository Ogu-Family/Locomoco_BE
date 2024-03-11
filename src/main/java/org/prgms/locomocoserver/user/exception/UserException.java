package org.prgms.locomocoserver.user.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
    private final UserErrorType errorType;

    public UserException(UserErrorType type) {
        super(type.getMessage());
        this.errorType = type;
    }
}
