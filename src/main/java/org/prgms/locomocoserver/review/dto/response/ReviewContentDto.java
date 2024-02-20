package org.prgms.locomocoserver.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.prgms.locomocoserver.review.domain.data.ReviewContent;

public record ReviewContentDto(
        @Schema(description = "리뷰 데이터 id", example = "1")
        Long reviewContentId,
        @Schema(description = "리뷰 데이터 내용", example = "응답이 빨라요")
        String content,
        @Schema(description = "긍정, 부정 리뷰 분류", example = "true")
        boolean isPositive
) {
    public static ReviewContentDto of(ReviewContent content) {
        return new ReviewContentDto(content.getId(), content.getContent(), content.isPositive());
    }
}
