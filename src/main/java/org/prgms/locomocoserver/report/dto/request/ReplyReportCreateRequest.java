package org.prgms.locomocoserver.report.dto.request;

import org.prgms.locomocoserver.replies.domain.Reply;
import org.prgms.locomocoserver.report.domain.ReplyReport;
import org.prgms.locomocoserver.user.domain.User;

public record ReplyReportCreateRequest(
        Long replyId,
        Long userId,
        String content
) {
    public static ReplyReport toEntity(User reporter, Reply reply, String content) {
        return ReplyReport.builder()
                .reporter(reporter)
                .reply(reply)
                .content(content)
                .build();
    }
}
