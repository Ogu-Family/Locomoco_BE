package org.prgms.locomocoserver.mogakkos.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.application.MogakkoLikeService;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoLikeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Mogakko Like Controller", description = "모각코 좋아요 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MogakkoLikeController {
    private final MogakkoLikeService mogakkoLikeService;

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
