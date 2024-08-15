package org.prgms.locomocoserver.report.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.report.application.ReplyReportService;
import org.prgms.locomocoserver.report.dto.ReplyReportDto;
import org.prgms.locomocoserver.report.dto.request.ReplyReportCreateRequest;
import org.prgms.locomocoserver.report.dto.request.ReplyReportUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Reply Report Controller", description = "댓글 신고 컨트롤러")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReplyReportController {

    private final ReplyReportService replyReportService;

    @Operation(summary = "댓글 신고 생성", description = "댓글 신고를 생성합니다.")
    @PostMapping("/reports/reply")
    public ResponseEntity<ReplyReportDto> create(@Valid @RequestBody ReplyReportCreateRequest request) {
        ReplyReportDto replyReportDto = replyReportService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(replyReportDto);
    }

    @Operation(summary = "댓글 신고 수정", description = "댓글 신고를 수정합니다.")
    @PatchMapping("/reports/reply/{id}")
    public ResponseEntity<ReplyReportDto> update(@PathVariable("reportId") Long id,
                                                 @Valid @RequestBody ReplyReportUpdateRequest request) {
        ReplyReportDto replyReportDto = replyReportService.update(id, request);
        return ResponseEntity.ok(replyReportDto);
    }

    @Operation(summary = "댓글 신고 조회", description = "전체 댓글 신고 목록을 조회합니다.")
    @GetMapping("/reports/reply")
    public ResponseEntity<List<ReplyReportDto>> getAllReplyReports(@RequestParam(required = false, defaultValue = "0") Long cursor,
                                                                   @RequestParam(defaultValue = "10") int pageSize) {
        List<ReplyReportDto> replyReportDtos = replyReportService.getAllReplyReports(cursor, pageSize);
        return ResponseEntity.ok(replyReportDtos);
    }

    @Operation(summary = "댓글 신고 삭제", description = "댓글 신고를 삭제합니다.")
    @DeleteMapping("/reports/reply/{id}")
    public ResponseEntity<Void> delete(@PathVariable("reportId") Long id) {
        replyReportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
