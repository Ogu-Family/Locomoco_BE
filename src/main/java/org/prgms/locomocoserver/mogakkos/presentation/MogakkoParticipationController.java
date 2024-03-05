package org.prgms.locomocoserver.mogakkos.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.application.MogakkoParticipationService;
import org.prgms.locomocoserver.mogakkos.dto.request.ParticipationRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.response.ParticipationCheckingDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Mogakko Participation controller", description = "모각코 참여 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MogakkoParticipationController {

    private final MogakkoParticipationService participationService;

    @Operation(summary = "모각코 참여 여부 확인", description = "어떤 모각코에 특정 유저가 참여 중인지를 확인할 수 있습니다.")
    @ApiResponses(
        @ApiResponse(responseCode = "200", description = "참여 확인 성공")
    )
    @GetMapping("/mogakko/map/{id}/participate")
    public ResponseEntity<ParticipationCheckingDto> checkParticipating(
        @Parameter(description = "모각코 id", example = "1") @PathVariable Long id,
        @Parameter(description = "참여 확인을 위한 유저 id", example = "2") @RequestParam Long userId) {
        ParticipationCheckingDto responseDto = participationService.check(id, userId);

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "모각코 참여 신청", description = "어떤 모각코에 특정 유저가 참여를 신청합니다.")
    @ApiResponses(
        @ApiResponse(responseCode = "200", description = "참여 성공")
    )
    @PostMapping("/mogakko/map/{id}/participate")
    public ResponseEntity<Void> participate(
        @Parameter(description = "모각코 id", example = "1") @PathVariable Long id,
        @Parameter(description = "참여를 원하는 유저 id") @RequestBody ParticipationRequestDto requestDto) {
        participationService.participate(id, requestDto);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "모각코 참여 취소", description = "어떤 모각코에 참여 중인 특정 유저가 해당 모각코 참여를 취소합니다.")
    @ApiResponses(
        @ApiResponse(responseCode = "204", description = "참여 취소 성공")
    )
    @DeleteMapping("/mogakko/map/{id}/participate")
    public ResponseEntity<Void> cancel(
        @Parameter(description = "모각코 id", example = "1") @PathVariable Long id,
        @Parameter(description = "참여 취소를 원하는 유저 id", example = "1") @RequestParam Long userId) {
        participationService.cancel(id, userId);

        return ResponseEntity.noContent().build();
    }
}
