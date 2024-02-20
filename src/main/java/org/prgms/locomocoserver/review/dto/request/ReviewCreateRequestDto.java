package org.prgms.locomocoserver.review.dto.request;

import java.util.List;

public record ReviewCreateRequestDto(
    Long reviewerId,
    Long revieweeId,
    List<Long> reviewContentId
) {
}
