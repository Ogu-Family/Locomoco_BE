package org.prgms.locomocoserver.report.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.report.application.ReportService;
import org.prgms.locomocoserver.report.dto.ReportDto;
import org.prgms.locomocoserver.report.dto.request.ReportCreateRequest;
import org.prgms.locomocoserver.report.dto.request.ReportUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/reports")
    public ResponseEntity<ReportDto> create(@Valid @RequestBody ReportCreateRequest request) {
        ReportDto reportDto = reportService.create(request);
        return ResponseEntity.ok(reportDto);
    }

    @PatchMapping("/reports/{id}")
    public ResponseEntity<ReportDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ReportUpdateRequest request) {
        ReportDto reportDto = reportService.update(id, request);
        return ResponseEntity.ok(reportDto);
    }

    @GetMapping("/reports")
    public ResponseEntity<List<ReportDto>> getAllReports() {
        List<ReportDto> reportDtos = reportService.getAllReports();
        return ResponseEntity.ok(reportDtos);
    }

}
