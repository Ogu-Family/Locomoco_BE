package org.prgms.locomocoserver.report.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.report.domain.Report;
import org.prgms.locomocoserver.report.domain.ReportRepository;
import org.prgms.locomocoserver.report.dto.ReportDto;
import org.prgms.locomocoserver.report.dto.request.ReportCreateRequest;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;

    public ReportDto create(ReportCreateRequest request) {
        User reporter = userService.getById(request.reporterId());
        Report report = reportRepository.save(request.toEntity(reporter));

        return ReportDto.of(report);
    }
}
