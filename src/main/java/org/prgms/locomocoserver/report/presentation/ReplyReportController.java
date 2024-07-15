package org.prgms.locomocoserver.report.presentation;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.report.application.ReplyReportService;
import org.prgms.locomocoserver.report.dto.ReplyReportDto;
import org.prgms.locomocoserver.report.dto.request.ReplyReportCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Reply Report Controller", description = "댓글 신고 컨트롤러")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReplyReportController {

    private final ReplyReportService replyReportService;

    @PostMapping("/reply/reports")
    public ResponseEntity<ReplyReportDto> create(@Valid @RequestBody ReplyReportCreateRequest request) {
        ReplyReportDto replyReportDto = replyReportService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(replyReportDto);
    }
}
