package org.prgms.locomocoserver.mogakkos.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MogakkoErrorType {
    NOT_FOUND(HttpStatus.NOT_FOUND, 700, "해당 모각코를 찾을 수 없습니다."),
    PROCESS_FORBIDDEN(HttpStatus.FORBIDDEN, 701, "해당 모각코 작성자만 처리할 수 있습니다."),
    CREATE_FORBIDDEN(HttpStatus.BAD_REQUEST, 702, "다음 이유로 생성이 불가능합니다. - "),
    TOO_LITTLE_INPUT(HttpStatus.BAD_REQUEST, 703, "입력 값이 최소한 다음 길이보다 길어야 합니다 - ");

    private HttpStatus httpStatus;
    private int code;
    private String message;

    MogakkoErrorType(HttpStatus httpStatus, int code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public MogakkoErrorType appendMessage(String msg) {
        this.message += msg;
        return this;
    }
}
