package org.prgms.locomocoserver.inquiries.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.inquiries.application.InquiryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Inquire Controller", description = "문의 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class InquiryController {
    private final InquiryService inquiryService;

    @Operation(summary = "문의 전체 조회", description = "문의 id로 문의 단건 조회를 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문의 단일 조회 성공"),
    })
    @Parameters({
            @Parameter(name = "id", description = "문의 id")
    })
    @GetMapping("/inquiries")
    public ResponseEntity<String> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body("문의 단건 조회");
    }
}
