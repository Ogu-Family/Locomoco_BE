package org.prgms.locomocoserver.review.dto.response;

import java.util.List;

public record ReviewDto(
        Long reviewId,
        Long reviewerId,
        Long revieweeId,
        List<String> reviewContent,
        String content
) {
}
