package org.prgms.locomocoserver.review.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.review.application.ReviewContentService;
import org.prgms.locomocoserver.review.application.ReviewService;
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
    private final ReviewService reviewService;

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
        ReviewDto reviewDto = reviewService.create(mogakkoId, reviewerId, request);

        return ResponseEntity.ok(reviewDto);
    }

    @GetMapping("/reviews/{userId}/recieved")
    @Operation(summary = "받은 리뷰 조회", description = "user id 기반으로 받은 리뷰를 조회 합니다.")
    public ResponseEntity<List<ReviewDto>> getRecievedReviews(@PathVariable Long userId) {
        List<ReviewDto> reviewDtos = reviewService.getRecievedReviews(userId);
        return ResponseEntity.ok(reviewDtos);
    }

    @GetMapping("/reviews/{userId}/sent")
    @Operation(summary = "보낸 리뷰 조회", description = "모각코 id, reviewee id 기반으로 받은 리뷰를 조회 합니다.")
    public ResponseEntity<List<ReviewDto>> getSentReviews(@PathVariable Long userId) {
        List<ReviewDto> reviewDtos = reviewService.getSentReviews(userId);
        return ResponseEntity.ok(reviewDtos);
    }

    @GetMapping("/reviews/mogakko/{mogakkoId}/recieved")
    @Operation(summary = "모각코 내가 받은 리뷰 조회", description = "모각코 안에서 내 리뷰를 모두 조회합니다.")
    public ResponseEntity<List<ReviewDto>> getMogakkoReviewsRecieved(@PathVariable Long mogakkoId,
                                                             @RequestParam Long revieweeId) {
        List<ReviewDto> reviewDtos = reviewService.getMogakkoReviewsRecieved(revieweeId, mogakkoId);
        return ResponseEntity.ok(reviewDtos);
    }

    @GetMapping("/reviews/mogakko/{mogakkoId}/sent")
    @Operation(summary = "모각코 내가 보낸 리뷰 조회", description = "모각코 안에서 내가 작성한 리뷰를 모두 조회합니다.")
    public ResponseEntity<List<ReviewDto>> getMogakkoReviewsSent(@PathVariable Long mogakkoId,
                                                             @RequestParam Long reviewerId) {
        List<ReviewDto> reviewDtos = reviewService.getMogakkoReviewsSent(reviewerId, mogakkoId);
        return ResponseEntity.ok(reviewDtos);
    }
}
