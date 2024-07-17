package org.prgms.locomocoserver.report.exception;

import org.springframework.http.HttpStatus;

public enum ReportErrorType {
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "신고할 댓글을 찾을 수 없습니다."),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "신고를 찾을 수 없습니다."),
    AUTH_NOT_ALLOWED(HttpStatus.UNAUTHORIZED, "작성자만 수정가능합니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    ReportErrorType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
