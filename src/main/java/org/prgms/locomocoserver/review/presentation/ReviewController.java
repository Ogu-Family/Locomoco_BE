package org.prgms.locomocoserver.review.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.review.application.ReviewContentService;
import org.prgms.locomocoserver.review.domain.data.ReviewContentRepository;
import org.prgms.locomocoserver.review.dto.request.ReviewCreateRequestDto;
import org.prgms.locomocoserver.review.dto.response.ReviewContentDto;
import org.prgms.locomocoserver.review.dto.response.ReviewDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Review Controller", description = "리뷰 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController {

    private final ReviewContentService reviewContentService;

    @Operation(summary = "리뷰 내용 전체 데이터 반환", description = "긍정리뷰, 부정리뷰 모든 리스트를 조회 합니다.")
    @GetMapping("/reviews/contents")
    public ResponseEntity<List<ReviewContentDto>> getAllReviewContents() {
        List<ReviewContentDto> reviewContentDtos = reviewContentService.getAllReviewContents();
        return ResponseEntity.ok(reviewContentDtos);
    }

    @Operation(summary = "리뷰 생성", description = "모각코 id, reviewer id 기반으로 리뷰를 생성합니다.")
    @PostMapping("/reviews/{mogakkoId}/{reviewerId}")
    public ResponseEntity<ReviewDto> createReview(@PathVariable Long mogakkoId, @PathVariable Long reviewerId,
                                               @RequestBody ReviewCreateRequestDto request) {
        // TODO: create review 로직 구현

        return ResponseEntity.ok(null);
    }
}