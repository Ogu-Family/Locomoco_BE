package org.prgms.locomocoserver.report.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.replies.domain.Reply;
import org.prgms.locomocoserver.replies.domain.ReplyRepository;
import org.prgms.locomocoserver.report.domain.ReplyReport;
import org.prgms.locomocoserver.report.domain.ReplyReportRepository;
import org.prgms.locomocoserver.report.dto.ReplyReportDto;
import org.prgms.locomocoserver.report.dto.request.ReplyReportCreateRequest;
import org.prgms.locomocoserver.report.exception.ReportErrorType;
import org.prgms.locomocoserver.report.exception.ReportException;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplyReportService {

    private final ReplyReportRepository replyReportRepository;
    private final UserService userService;
    private final ReplyRepository replyRepository;

    public ReplyReportDto create(ReplyReportCreateRequest request) {
        User reporter = userService.getById(request.userId());
        Reply reply = replyRepository.findByIdAndDeletedAtIsNotNull(request.replyId())
                .orElseThrow(() -> new ReportException(ReportErrorType.REPLY_NOT_FOUND));
        ReplyReport replyReport = replyReportRepository.save(request.toEntity(reporter, reply, request.content()));

        return ReplyReportDto.of(replyReport);
    }
}
