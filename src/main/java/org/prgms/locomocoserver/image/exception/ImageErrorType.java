package org.prgms.locomocoserver.image.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ImageErrorType {
    FILE_WRITE_ERROR(HttpStatus.MULTI_STATUS, "파일 쓰기에 실패했습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지가 존재하지 않습니다.")
    ;

    private final HttpStatus status;
    private final String message;

    ImageErrorType(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }
}
