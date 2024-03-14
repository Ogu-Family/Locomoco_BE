package org.prgms.locomocoserver.chat.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChatErrorType {
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "Chat Room does not exist")
    ;

    private final HttpStatus status;
    private final String message;

    ChatErrorType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
