package org.prgms.locomocoserver.report.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.report.application.ReportService;
import org.prgms.locomocoserver.report.dto.ReportDto;
import org.prgms.locomocoserver.report.dto.request.ReportCreateRequest;
import org.prgms.locomocoserver.report.dto.request.ReportUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Report Controller", description = "신고 컨트롤러")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "신고 생성", description = "신고를 생성합니다.")
    @PostMapping("/reports")
    public ResponseEntity<ReportDto> create(@Valid @RequestBody ReportCreateRequest request) {
        ReportDto reportDto = reportService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reportDto);
    }

    @Operation(summary = "신고 수정", description = "신고 내용을 수정합니다.")
    @PatchMapping("/reports/{id}")
    public ResponseEntity<ReportDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ReportUpdateRequest request) {
        ReportDto reportDto = reportService.update(id, request);
        return ResponseEntity.ok(reportDto);
    }

    @Operation(summary = "신고 전체 조회", description = "신고한 모든 목록을 조회합니다.")
    @GetMapping("/reports")
    public ResponseEntity<List<ReportDto>> getAllReports(@RequestParam(name = "cursor", required = false) Long cursor,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        List<ReportDto> reportDtos = reportService.getAllReports(cursor, pageSize);
        return ResponseEntity.ok(reportDtos);
    }

    @Operation(summary = "신고 삭제", description = "신고 내용을 삭제합니다.")
    @DeleteMapping("/reports/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reportService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
