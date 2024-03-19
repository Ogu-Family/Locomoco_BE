package org.prgms.locomocoserver.review.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReviewErrorType {
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 리뷰가 존재하지 않습니다.")
    ;

    private final HttpStatus status;
    private final String message;

    ReviewErrorType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
