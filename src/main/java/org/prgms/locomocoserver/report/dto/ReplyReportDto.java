package org.prgms.locomocoserver.report.dto;

import org.prgms.locomocoserver.report.domain.ReplyReport;

public record ReplyReportDto(
        Long id,
        Long reporter,
        Long replyId,
        String content
) {
    public static ReplyReportDto of(ReplyReport replyReport) {
        return new ReplyReportDto(replyReport.getId(), replyReport.getReporter().getId(), replyReport.getId(), replyReport.getContent());
    }
}
