package org.prgms.locomocoserver.inquiries.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        @ApiResponse(responseCode = "200", description = "문의 전체 조회 성공"),
    })
    @Parameters({
        @Parameter(name = "cursor", description = "인덱스 커서. 마지막으로 조회한 문의 id. default Value는 사실 9223372036854775807여야 합니다. 스웨거 UI 오류임."),
        @Parameter(name = "mogakkoId", description = "연관된 모각코 id"),
        @Parameter(name = "userId", description = "연관된 작성자 id")
    })
    @GetMapping("/inquiries")
    public ResponseEntity<Results<InquiryResponseDto>> findAll(
        @RequestParam(defaultValue = "9223372036854775807") Long cursor,
        @RequestParam(required = false) Long mogakkoId,
        @RequestParam(required = false) Long userId) {
        log.info("cursor: {}", cursor);

        List<InquiryResponseDto> responseDtos = inquiryService.findAll(cursor, mogakkoId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(new Results<>(responseDtos));
    }

    @Operation(summary = "문의 생성", description = "작성자 id, 모각코 id에 따라 문의를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "문의 생성 성공"),
    })
    @PutMapping("/inquries")
    public ResponseEntity<Void> create(
        @Parameter(description = "문의 생성을 위해 보내주는 정보") @RequestBody InquiryCreateRequestDto requestDto) {
        inquiryService.save(requestDto);

        return ResponseEntity.ok(null);
    }

    @Operation(summary = "문의 수정", description = "작성자 id, 문의 id에 따라 문의를 수정합니다. 작성자 id는 검증용으로 이용합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "문의 수정 성공"),
    })
    @PatchMapping("/inquiries/{inquiryId}")
    public ResponseEntity<InquiryUpdateResponseDto> update(
        @Parameter(description = "문의 id") @PathVariable(name = "inquiryId") Long id,
        @Parameter(description = "업데이트 정보") @RequestBody InquiryUpdateRequestDto requestDto) {
        InquiryUpdateResponseDto responseDto = inquiryService.update(id, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "문의 삭제", description = "유저 id, 문의 id에 따라 문의를 삭제합니다. 유저 id는 문의 작성자인지 검증하기 위해 이용합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "문의 삭제 성공"),
    })
    @DeleteMapping("/inquiries/{inquiryId}")
    public ResponseEntity<Void> delete(
        @Parameter(description = "문의 id") @PathVariable Long inquiryId,
        @Parameter(description = "유저 id") @RequestParam Long userId) {
        inquiryService.delete(inquiryId, userId);

        return ResponseEntity.noContent().build();
    }
}
