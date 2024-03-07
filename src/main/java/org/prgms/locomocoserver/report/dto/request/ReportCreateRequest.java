package org.prgms.locomocoserver.report.dto.request;

import lombok.NonNull;
import org.prgms.locomocoserver.report.domain.Report;
import org.prgms.locomocoserver.user.domain.User;

public record ReportCreateRequest(
        @NonNull
        Long reporterId,
        @NonNull
        Long reportedId,
        String content
) {
    public Report toEntity(User reporter) {
        return Report.builder()
                .reporter(reporter)
                .reportedId(reportedId)
                .content(content).build();
    }
}
