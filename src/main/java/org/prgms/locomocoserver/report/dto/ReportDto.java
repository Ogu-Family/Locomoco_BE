package org.prgms.locomocoserver.report.dto;

import org.prgms.locomocoserver.report.domain.Report;

public record ReportDto(
        Long reportId,
        Long reporterId,
        Long reportedId,
        String content
) {
    public static ReportDto of(Report report) {
        return new ReportDto(
                report.getId(),
                report.getReporter().getId(),
                report.getReportedId(),
                report.getContent()
        );
    }
}
