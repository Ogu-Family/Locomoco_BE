package org.prgms.locomocoserver.chat.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChatErrorType {
    NO_CONTENT(HttpStatus.NO_CONTENT, "Chat Message has No Content")
    ;

    private final HttpStatus status;
    private final String message;

    ChatErrorType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
