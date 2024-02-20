package org.prgms.locomocoserver.review.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.review.dto.request.ReviewCreateRequestDto;
import org.prgms.locomocoserver.review.dto.response.ReviewDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review Controller", description = "리뷰 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController {

    @Operation(summary = "리뷰 생성", description = "모각코 id, reviewer id 기반으로 리뷰를 생성합니다.")
    @PostMapping("/reviews/{mogakkoId}/{reviewerId}")
    public ResponseEntity<ReviewDto> createReview(@PathVariable Long mogakkoId, @PathVariable Long reviewerId,
                                               @RequestBody ReviewCreateRequestDto request) {
        // TODO: create review 로직 구현

        return ResponseEntity.ok(null);
    }
}
