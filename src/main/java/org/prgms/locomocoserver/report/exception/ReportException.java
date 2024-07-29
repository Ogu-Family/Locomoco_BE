package org.prgms.locomocoserver.report.exception;

import lombok.Getter;

@Getter
public class ReportException extends RuntimeException{
    private final ReportErrorType errorType;

    public ReportException(ReportErrorType errorType) {
        this.errorType = errorType;
    }
}
