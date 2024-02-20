package org.prgms.locomocoserver.review.dto.response;

import org.prgms.locomocoserver.review.domain.data.ReviewContent;

public record ReviewContentDto(
        Long reviewContentId,
        String content,
        boolean isPositive
) {
    public static ReviewContentDto of(ReviewContent content) {
        return new ReviewContentDto(content.getId(), content.getContent(), content.isPositive());
    }
}
