package org.prgms.locomocoserver.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.prgms.locomocoserver.report.domain.Report;
import org.prgms.locomocoserver.user.domain.User;

public record ReportDto(
        @Schema(description = "신고 id") Long reportId,
        @Schema(description = "신고자 id") Long reporterId,
        @Schema(description = "신고 당한 사람 id") Long reportedId,
        @Schema(description = "신고 당한 사람 닉네임") String reportedNickname,
        @Schema(description = "신고 내용") String content
) {
    public static ReportDto of(Report report, User reported) {
        return new ReportDto(
                report.getId(),
                report.getReporter().getId(),
                report.getReportedId(),
                reported.getNickname(),
                report.getContent()
        );
    }
}
