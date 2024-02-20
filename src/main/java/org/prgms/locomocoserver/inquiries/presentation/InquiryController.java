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
import org.prgms.locomocoserver.inquiries.dto.request.InquiryCreateRequestDto;
import org.prgms.locomocoserver.inquiries.dto.request.InquiryUpdateRequestDto;
import org.prgms.locomocoserver.inquiries.dto.response.InquiryResponseDto;
import org.prgms.locomocoserver.inquiries.dto.response.InquiryUpdateResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
        @RequestParam(required = false) Long userId) { // TODO: 실 구현 필요
        ArrayList<InquiryResponseDto> responseDtos = new ArrayList<>();

        IntStream.range(0, 10).forEach(i -> responseDtos.add(
            new InquiryResponseDto((long) i + 1, "어딘가", "닉넴" + i, LocalDateTime.now().truncatedTo(
                ChronoUnit.NANOS), LocalDateTime.now().truncatedTo(ChronoUnit.NANOS), "내용",
                List.of((long) i + 1, (long) i + 2))));

        log.info("mogakkoId = {}, userId = {}", mogakkoId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(new Results<>(responseDtos));
    }

    @Operation(summary = "문의 생성", description = "작성자 id, 모각코 id에 따라 문의를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "문의 생성 성공"),
    })
    @PutMapping("/inquries")
    public ResponseEntity<Void> create(
        @Parameter(description = "문의 생성을 위해 보내주는 정보") @RequestBody InquiryCreateRequestDto requestDto) { // TODO: 실 구현 필요
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "문의 수정", description = "작성자 id, 문의 id에 따라 문의를 수정합니다. 작성자 id는 검증용으로 이용합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "문의 수정 성공"),
    })
    @PatchMapping("/inquiries")
    public ResponseEntity<InquiryUpdateResponseDto> update(@RequestBody InquiryUpdateRequestDto requestDto) { // TODO: 실 구현 필요
        InquiryUpdateResponseDto responseDto = new InquiryUpdateResponseDto(1L);

        return ResponseEntity.ok(responseDto);
    }
}
