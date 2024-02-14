package org.prgms.locomocoserver.mogakkos.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.application.MogakkoService;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoCreateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoCreateResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Mogakko controller", description = "모각코 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MogakkoController {

    private final MogakkoService mogakkoService;

    @PostMapping("/mogakko/map")
    @Operation(summary = "모각코 생성", description = "생성에 필요한 값을 받아 모각코를 생성합니다.")
    @ApiResponses(
        @ApiResponse(responseCode = "200", description = "모각코 생성 성공")
    )
    public ResponseEntity<MogakkoCreateResponseDto> create(@RequestBody MogakkoCreateRequestDto requestDto) {
        MogakkoCreateResponseDto responseDto = mogakkoService.create(requestDto);

        return ResponseEntity.ok(responseDto);
    }
}
