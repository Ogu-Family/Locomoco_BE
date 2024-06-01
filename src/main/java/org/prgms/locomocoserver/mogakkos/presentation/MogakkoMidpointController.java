package org.prgms.locomocoserver.mogakkos.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.application.MidpointService;
import org.prgms.locomocoserver.mogakkos.dto.response.MidpointDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Mogakko Midpoint Controller", description = "모각코 중간 지점 컨트롤러")
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class MogakkoMidpointController {
    private final MidpointService midpointService;

    @Operation(summary = "모각코 중간지점 도출", description = "모각코 참여자들의 최적의 중간지점을 찾아 반환합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "중간 지점 생성 성공"),
        @ApiResponse(responseCode = "500", description = "생성 중 에러 발생", content = @Content(
                mediaType = "plain/text", examples = @ExampleObject(value = "적절한 중간 지점을 생성할 수 없습니다!")
            )
        )
    })
    @GetMapping("/mogakko/recommend")
    public ResponseEntity<MidpointDto> recommend(
        @Parameter(description = "중간 지점을 찾을 모각코 id") @RequestParam long mogakkoId) {
        MidpointDto midpointDto = midpointService.recommend(mogakkoId);

        return ResponseEntity.ok(midpointDto);
    }

}
