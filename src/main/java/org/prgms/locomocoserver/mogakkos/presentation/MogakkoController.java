package org.prgms.locomocoserver.mogakkos.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.application.MogakkoService;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoCreateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoUpdateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoCreateResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoDetailResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoUpdateResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        MogakkoCreateResponseDto responseDto = mogakkoService.save(requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/mogakko/map/{id}")
    @Operation(summary = "모각코 디테일 정보 반환", description = "모각코 디테일 페이지에 사용자가 접속하면, 해당 모각코 관련 정보들을 띄웁니다. 정보에는 생성자 정보, 참여자 정보, 모각코 정보가 포함되어 있습니다.")
    @ApiResponses(
        @ApiResponse(responseCode = "200", description = "모각코 디테일 조회 성공")
    )
    public ResponseEntity<MogakkoDetailResponseDto> findDetail(
        @Parameter(description = "조회할 모각코 id") @PathVariable Long id) {
        MogakkoDetailResponseDto responseDto = mogakkoService.findDetail(id);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/mogakko/map/{id}")
    @Operation(summary = "모각코 수정", description = "수정할 정보를 넘겨주면 해당 내용을 기반으로 DB에서 값을 수정하고 응답 값으로 수정된 모각코 id를 넘겨줍니다.")
    @ApiResponses(
        @ApiResponse(responseCode = "200", description = "모각코 수정 성공")
    )
    public ResponseEntity<MogakkoUpdateResponseDto> update(
        @RequestBody MogakkoUpdateRequestDto requestDto, @PathVariable Long id) {
        MogakkoUpdateResponseDto responseDto = mogakkoService.update(requestDto, id);

        return ResponseEntity.ok(responseDto);
    }
}
