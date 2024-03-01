package org.prgms.locomocoserver.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.review.domain.Review;
import org.prgms.locomocoserver.user.domain.User;

import java.util.List;

public record ReviewCreateRequestDto(
        @Schema(description = "리뷰 대상자 id", example = "3")
        Long revieweeId,
        @Schema(description = "리뷰 대상자 차단 여부", example = "false")
        boolean blockDesired,
        @Schema(description = "별점", example = "4")
        int score,
        @Schema(description = "리뷰 체크박스 id 리스트", example = "[1, 3, 5]")
        List<Long> reviewContentId,
        @Schema(description = "리뷰 내용", example = "와우")
        String content
) {
        public static Review create(Mogakko mogakko, User reviewer, User reviewee, ReviewCreateRequestDto requestDto) {
                return Review.builder()
                        .reviewer(reviewer)
                        .reviewee(reviewee)
                        .content(requestDto.content())
                        .score(requestDto.score())
                        .reviewContentIds(requestDto.reviewContentId()).build();
        }
}
