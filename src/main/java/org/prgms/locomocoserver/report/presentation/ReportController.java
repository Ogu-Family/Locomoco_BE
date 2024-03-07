package org.prgms.locomocoserver.report.presentation;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.report.application.ReportService;
import org.prgms.locomocoserver.report.dto.ReportDto;
import org.prgms.locomocoserver.report.dto.request.ReportCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/reports")
    public ResponseEntity<ReportDto> create(@RequestBody ReportCreateRequest request) {
        ReportDto reportDto = reportService.create(request);
        return ResponseEntity.ok(reportDto);
    }
}
