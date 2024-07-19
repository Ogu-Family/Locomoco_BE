package org.prgms.locomocoserver.report.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;
import org.prgms.locomocoserver.report.domain.Report;
import org.prgms.locomocoserver.report.domain.UserReport;
import org.prgms.locomocoserver.user.domain.User;

public record ReportCreateRequest(
        @Schema(description = "신고자 id") @NonNull
        Long reporterId,
        @Schema(description = "신고 당한 사람 id") @NonNull
        Long reportedId,
        @Schema(description = "신고 내용")
        String content
) {
    public UserReport toEntity(User reporter, User reported) {
        return UserReport.builder()
                .reporter(reporter)
                .reportedUser(reported)
                .content(content).build();
    }
}
