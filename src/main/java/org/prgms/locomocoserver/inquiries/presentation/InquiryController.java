package org.prgms.locomocoserver.inquiries.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.global.common.dto.Results;
import org.prgms.locomocoserver.inquiries.application.InquiryService;
import org.prgms.locomocoserver.inquiries.domain.Inquiry;
import org.prgms.locomocoserver.inquiries.dto.response.InquiryResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Inquire Controller", description = "문의 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class InquiryController {
    private final InquiryService inquiryService;

    @Operation(summary = "문의 전체 조회", description = "모각코 id, 작성자 id에 해당하는 문의들을 전부 가져옵니다. 각 파라미터는 필수 인자가 아니며, 모든 파라미터를 넘겨주지 않으면 전체 문의가 조회됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문의 단일 조회 성공"),
    })
    @Parameters({
            @Parameter(name = "mogakkoId", description = "연관된 모각코 id"),
            @Parameter(name = "userId", description = "연관된 작성자 id")
    })
    @GetMapping("/inquiries")
    public ResponseEntity<Results<InquiryResponseDto>> findAll(
        @RequestParam(required = false) Long mogakkoId,
        @RequestParam(required = false) Long userId) {
        ArrayList<InquiryResponseDto> responseDtos = new ArrayList<>();

        IntStream.range(0, 10).forEach(i -> responseDtos.add(new InquiryResponseDto((long)i + 1, "어딘가", "닉넴" + i, LocalDateTime.now().truncatedTo(
            ChronoUnit.NANOS), LocalDateTime.now().truncatedTo(ChronoUnit.NANOS), "내용", List.of((long)i + 1, (long)i + 2))));

        log.info("mogakkoId = {}, userId = {}", mogakkoId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(new Results<>(responseDtos));
    }
}
