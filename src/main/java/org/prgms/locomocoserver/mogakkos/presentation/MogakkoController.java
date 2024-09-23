package org.prgms.locomocoserver.mogakkos.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.global.common.dto.Results;
import org.prgms.locomocoserver.mogakkos.application.MogakkoService;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoCreateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoUpdateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.request.SearchConditionDto;
import org.prgms.locomocoserver.mogakkos.dto.request.SearchParameterDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoCreateResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoDetailResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoSimpleInfoResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoUpdateResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Mogakko Controller", description = "모각코 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MogakkoController {
    private final MogakkoService mogakkoService;

    @GetMapping("/mogakko/map")
    @Operation(summary = "모각코 리스트 반환", description = "홈 화면(리스트 화면)에서 필터링된 모각코 리스트를 반환합니다. 종료되었거나 삭제된 모각코는 보이지 않습니다. cursor default value는 실제로는 9223372036854775807입니다.")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "모각코 목록 반환 성공")
    )
    public ResponseEntity<Results<MogakkoSimpleInfoResponseDto>> findAll(
            @Parameter(description = "제목+내용 세부 필터링 값") @RequestParam(name = "titleAndContent", required = false, defaultValue = "") String titleAndContent,
            @Parameter(description = "장소 세부 필터링 값") @RequestParam(name = "location", required = false, defaultValue = "") String location,
            @Parameter(description = "닉네임 세부 필터링 값") @RequestParam(name = "nickname", required = false, defaultValue = "") String nickname,
            @Parameter(description = "검색 오프셋") @RequestParam(name = "offset", required = false, defaultValue = "0") Long offset,
            @Parameter(description = "페이지 크기") @RequestParam(name = "pageSize", defaultValue = "100") Integer pageSize,
            @Parameter(description = "필터링 태그 id 목록") @RequestParam(name = "tags", required = false) List<Long> tags) {
        SearchParameterDto searchParameterDto = new SearchParameterDto(titleAndContent,
            location, nickname, tags);
        SearchConditionDto searchConditionDto = new SearchConditionDto(LocalDateTime.now(), offset,
            pageSize);

        List<MogakkoSimpleInfoResponseDto> responseDtos = mogakkoService.findAll(searchParameterDto, searchConditionDto);

        Results<MogakkoSimpleInfoResponseDto> results = new Results<>(responseDtos);

        return ResponseEntity.ok(results);
    }

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
            @RequestBody MogakkoUpdateRequestDto requestDto,
            @Parameter(description = "수정하려는 모각코 id") @PathVariable Long id) {
        MogakkoUpdateResponseDto responseDto = mogakkoService.update(requestDto, id);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/mogakko/map/{id}")
    @Operation(summary = "모각코 삭제", description = "모각코 id 값을 넘겨 해당 모각코를 삭제합니다. DB에는 존재하며 삭제된 상태로만 기록됩니다.")
    @ApiResponses(
            @ApiResponse(responseCode = "204", description = "모각코 삭제 성공")
    )
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 모각코 id") @PathVariable Long id) {
        mogakkoService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
