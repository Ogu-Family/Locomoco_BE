package org.prgms.locomocoserver.review.presentation;

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

    @PostMapping("/reviews/{mogakkoId}/{sendorId}")
    public ResponseEntity<ReviewDto> createReview(@PathVariable Long mogakkoId, @PathVariable Long sendorId,
                                               @RequestBody ReviewCreateRequestDto request) {
        // TODO: create review 로직 구현

        return ResponseEntity.ok(null);
    }
}
