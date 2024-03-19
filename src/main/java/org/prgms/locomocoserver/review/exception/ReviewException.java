package org.prgms.locomocoserver.review.exception;

import lombok.Getter;

@Getter
public class ReviewException extends RuntimeException {
    private final ReviewErrorType errorType;

    public ReviewException(ReviewErrorType type) {
        super(type.getMessage());
        this.errorType = type;
    }
}
