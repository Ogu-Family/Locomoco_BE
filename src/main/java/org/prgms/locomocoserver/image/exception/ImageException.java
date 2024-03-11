package org.prgms.locomocoserver.image.exception;

import lombok.Getter;

@Getter
public class ImageException extends RuntimeException {

    private final ImageErrorType errorType;

    public ImageException(ImageErrorType type) {
        super(type.getMessage());
        this.errorType = type;
    }
}
