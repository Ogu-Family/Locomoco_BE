package org.prgms.locomocoserver.mogakkos.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.global.common.dto.Results;
import org.prgms.locomocoserver.mogakkos.application.MogakkoLikeService;
import org.prgms.locomocoserver.mogakkos.application.MogakkoService;
import org.prgms.locomocoserver.mogakkos.application.SearchType;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoCreateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoUpdateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoDetailResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoLikeDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoSimpleInfoResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoUpdateResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Mogakko controller", description = "모각코 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MogakkoController {

    private final MogakkoService mogakkoService;
    private final MogakkoLikeService mogakkoLikeService;

    @GetMapping("/mogakko/map")
    @Operation(summary = "모각코 리스트 반환", description = "홈 화면(리스트 화면)에서 필터링된 모각코 리스트를 반환합니다.")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "모각코 목록 반환 성공")
    )
    public ResponseEntity<Results<MogakkoSimpleInfoResponseDto>> findAll(
            @Parameter(description = "필터링 커서") @RequestParam(required = false, defaultValue = "0") Long cursor,
            @Parameter(description = "검색 값") @RequestParam(name = "search", required = false, defaultValue = "") String searchVal,
            @Parameter(description = "검색 타입") @RequestParam SearchType searchType,
            @Parameter(description = "필터링 태그 id 목록") @RequestParam(required = false) List<Long> tags) {
        searchVal = searchVal.strip();

        List<MogakkoSimpleInfoResponseDto> responseDtos = mogakkoService.findAllByFilter(tags,
                cursor, searchVal, searchType);

        Results<MogakkoSimpleInfoResponseDto> results = new Results<>(responseDtos);

        return ResponseEntity.ok(results);
    }

    @PutMapping("/mogakko/map")
    @Operation(summary = "모각코 생성", description = "생성에 필요한 값을 받아 모각코를 생성합니다.")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "모각코 생성 성공")
    )
    public ResponseEntity<Void> create(@RequestBody MogakkoCreateRequestDto requestDto) {
        mogakkoService.save(requestDto);

        return ResponseEntity.ok(null);
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

    @PostMapping("/mogakko/{id}/like")
    @Operation(summary = "모각코 좋아요", description = "좋아요")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "모각코 좋아요 성공")
    )
    public ResponseEntity<MogakkoLikeDto> like(
            @Parameter(description = "좋아요 할 모각코 id") @PathVariable Long id,
            @Parameter(description = "좋아요 요청한 사용자 id") @RequestParam Long userId) {
        MogakkoLikeDto mogakkoLikeDto = mogakkoLikeService.like(id, userId);

        return ResponseEntity.ok(mogakkoLikeDto);
    }

    @DeleteMapping("/mogakko/{id}/like")
    @Operation(summary = "모각코 좋아요 취소", description = "좋아요 취소")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "좋아요 취소 성공")
    )
    public ResponseEntity<MogakkoLikeDto> likeCancel(
            @Parameter(description = "좋아요 취소 할 모각코 id") @PathVariable Long id,
            @Parameter(description = "좋아요 취소 요청한 사용자 id") @RequestParam Long userId) {
        MogakkoLikeDto mogakkoLikeDto = mogakkoLikeService.likeCancel(id, userId);

        return ResponseEntity.ok(mogakkoLikeDto);
    }
}
