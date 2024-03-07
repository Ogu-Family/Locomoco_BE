package org.prgms.locomocoserver.report.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.report.domain.Report;
import org.prgms.locomocoserver.report.domain.ReportRepository;
import org.prgms.locomocoserver.report.dto.ReportDto;
import org.prgms.locomocoserver.report.dto.request.ReportCreateRequest;
import org.prgms.locomocoserver.report.dto.request.ReportUpdateRequest;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public ReportDto update(Long id, ReportUpdateRequest request) {
        Report report = getById(id);
        report.updateContent(request.content());
        return ReportDto.of(report);
    }

    @Transactional(readOnly = true)
    public List<ReportDto> getAllReports(Long cursor, int pageSize) {
        if (cursor == null) cursor = 0L;
        return reportRepository.findAllByDeletedAtIsNull(cursor, pageSize).stream()
              .map(report -> ReportDto.of(report))
              .toList();
    }

    public void delete(Long id) {
        Report report = getById(id);
        reportRepository.delete(report);
    }

    private Report getById(Long id) {
        return reportRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Report Not Found [id]: " + id));
    }
}
