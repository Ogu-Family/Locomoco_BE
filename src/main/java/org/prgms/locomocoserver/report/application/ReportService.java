package org.prgms.locomocoserver.report.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.report.domain.Report;
import org.prgms.locomocoserver.report.domain.UserReport;
import org.prgms.locomocoserver.report.domain.UserReportRepository;
import org.prgms.locomocoserver.report.dto.ReportDto;
import org.prgms.locomocoserver.report.dto.request.ReportCreateRequest;
import org.prgms.locomocoserver.report.dto.request.ReportUpdateRequest;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserReportRepository userReportRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public ReportDto create(ReportCreateRequest request) {
        User reporter = userService.getById(request.reporterId());
        User reported = userService.getById(request.reportedId());
        Report report = userReportRepository.save(request.toEntity(reporter, reported));

        return ReportDto.of(report, reported);
    }

    @Transactional
    public ReportDto update(Long id, ReportUpdateRequest request) {
        UserReport report = getById(id);
        report.updateContent(request.content());
        User reported = userService.getById(report.getReportedUser().getId());

        return ReportDto.of(report, reported);
    }

    @Transactional(readOnly = true)
    public List<ReportDto> getAllReports(Long cursor, int pageSize) {
        if (cursor == null) cursor = 0L;
        return userReportRepository.findAllByDeletedAtIsNull(cursor, pageSize).stream()
              .map(report -> {
                  User reported = userRepository.findById(report.getReportedUser().getId())
                          .orElseThrow(() -> new UserException(UserErrorType.USER_NOT_FOUND));
                  return ReportDto.of(report, reported);
              })
              .toList();
    }

    public void delete(Long id) {
        UserReport report = getById(id);
        userReportRepository.delete(report);
    }

    private UserReport getById(Long id) {
        return userReportRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Report Not Found [id]: " + id));
    }
}
