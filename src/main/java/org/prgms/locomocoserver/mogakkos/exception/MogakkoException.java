package org.prgms.locomocoserver.mogakkos.exception;

import lombok.Getter;

@Getter
public class MogakkoException extends RuntimeException{
    private final MogakkoErrorType errorType;

    public MogakkoException(MogakkoErrorType errorType) {
        this.errorType = errorType;
    }
}
