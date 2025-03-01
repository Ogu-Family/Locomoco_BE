package org.prgms.locomocoserver.chat.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChatErrorType {
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다."),
    CHAT_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지가 존재하지 않습니다."),
    CHAT_PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방에 참여중인 사용자가 없습니다."),
    ;

    private final HttpStatus status;
    private final String message;

    ChatErrorType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
