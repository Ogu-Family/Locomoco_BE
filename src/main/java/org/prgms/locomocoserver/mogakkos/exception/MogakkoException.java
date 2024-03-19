package org.prgms.locomocoserver.mogakkos.exception;

import lombok.Getter;

@Getter
public class MogakkoException extends RuntimeException{
    private final MogakkoErrorCode errorCode;

    public MogakkoException(MogakkoErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
