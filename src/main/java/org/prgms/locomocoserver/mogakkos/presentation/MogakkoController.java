package org.prgms.locomocoserver.mogakkos.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.global.common.dto.Results;
import org.prgms.locomocoserver.location.dto.LocationInfoDto;
import org.prgms.locomocoserver.mogakkos.application.MogakkoService;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoCreateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoUpdateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoDetailResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoSimpleInfoResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoUpdateResponseDto;
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

@Tag(name = "Mogakko controller", description = "모각코 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MogakkoController {

    private final MogakkoService mogakkoService;

    @GetMapping("/mogakko/map")
    @Operation(summary = "모각코 리스트 반환", description = "홈 화면(리스트 화면)에서 필터링된 모각코 리스트를 반환합니다.")
    @ApiResponses(
        @ApiResponse(responseCode = "200", description = "모각코 목록 반환 성공")
    )
    public ResponseEntity<Results<MogakkoSimpleInfoResponseDto>> findAll(
        @Parameter(description = "필터링 태그 id 목록") @RequestParam List<Long> tags) { // TODO: 실 구현 필요
        ArrayList<MogakkoSimpleInfoResponseDto> responseDtos = new ArrayList<>();

        IntStream.range(0, 10).forEach(i -> responseDtos.add(
            new MogakkoSimpleInfoResponseDto("제모옥" + i, i * 10, i * 3, i % 8 + 2, 1,
                new LocationInfoDto("어딘가", (double) 101 / (i + 1), (double) 157 / (i + 1), "개봉동"), List.of(
                (long) i, (long) i + 1, (long) i + 2))));

        Results<MogakkoSimpleInfoResponseDto> responseDto = new Results<>(responseDtos);

        return ResponseEntity.ok(responseDto);
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
}
