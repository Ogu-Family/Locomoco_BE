package org.prgms.locomocoserver.report.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.replies.domain.Reply;
import org.prgms.locomocoserver.replies.domain.ReplyRepository;
import org.prgms.locomocoserver.report.domain.ReplyReport;
import org.prgms.locomocoserver.report.domain.ReplyReportRepository;
import org.prgms.locomocoserver.report.domain.Report;
import org.prgms.locomocoserver.report.dto.ReplyReportDto;
import org.prgms.locomocoserver.report.dto.request.ReplyReportCreateRequest;
import org.prgms.locomocoserver.report.dto.request.ReplyReportUpdateRequest;
import org.prgms.locomocoserver.report.exception.ReportErrorType;
import org.prgms.locomocoserver.report.exception.ReportException;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyReportService {

    private final ReplyReportRepository replyReportRepository;
    private final UserService userService;
    private final ReplyRepository replyRepository;

    public ReplyReportDto create(ReplyReportCreateRequest request) {
        User reporter = userService.getById(request.userId());
        Reply reply = replyRepository.findByIdAndDeletedAtIsNull(request.replyId())
                .orElseThrow(() -> new ReportException(ReportErrorType.REPLY_NOT_FOUND));
        ReplyReport replyReport = replyReportRepository.save(request.toEntity(reporter, reply, request.content()));

        return ReplyReportDto.of(replyReport);
    }

    @Transactional
    public ReplyReportDto update(Long id, ReplyReportUpdateRequest request) {
        ReplyReport replyReport = getById(id);

        if(!replyReport.getReporter().getId().equals(request.reporterId())) {
            throw new ReportException(ReportErrorType.AUTH_NOT_ALLOWED);
        }
        replyReport.updateContent(request.content());

        return ReplyReportDto.of(replyReport);
    }

    @Transactional(readOnly = true)
    public List<ReplyReportDto> getAllReplyReports(Long cursor, int pageSize) {
        return replyReportRepository.findAllByDeletedAtIsNull(cursor, pageSize).stream()
                .map(replyReport -> ReplyReportDto.of(replyReport))
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        ReplyReport replyReport = getById(id);
        replyReport.delete();
    }

    @Transactional(readOnly = true)
    public ReplyReport getById(Long id) {
        return replyReportRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ReportException(ReportErrorType.REPORT_NOT_FOUND));
    }
}
