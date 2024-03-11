package org.prgms.locomocoserver.image.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ImageErrorType {
    FILE_WRITE_ERROR(HttpStatus.MULTI_STATUS, "File Write Error");

    private final HttpStatus status;
    private final String message;

    ImageErrorType(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }
}
