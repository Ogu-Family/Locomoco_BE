package org.prgms.locomocoserver.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ReviewDto(
        @Schema(description = "리뷰 id", example = "1")
        Long reviewId,
        @Schema(description = "리뷰자 id", example = "5")
        Long reviewerId,
        @Schema(description = "리뷰 대상자 id", example = "2")
        Long revieweeId,
        @Schema(description = "별점", example = "4")
        int score,
        @Schema(description = "체크박스 리뷰 id 리스트", example = "[1, 2, 3]")
        List<Long> reviewContentId,
        @Schema(description = "리뷰 내용", example = "좋았습니다~")
        String content
) {
}
