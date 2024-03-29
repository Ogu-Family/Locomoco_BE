package org.prgms.locomocoserver.chat.exception;

import lombok.Getter;

@Getter
public class ChatException extends RuntimeException{
    private final ChatErrorType errorType;

    public ChatException(ChatErrorType type) {
        super(type.getMessage());
        this.errorType = type;
    }

    public ChatException(ChatErrorType type, String message) {
        super(message);
        this.errorType = type;
    }
}
