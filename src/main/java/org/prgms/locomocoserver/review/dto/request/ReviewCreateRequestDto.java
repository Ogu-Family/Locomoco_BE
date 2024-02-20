package org.prgms.locomocoserver.review.dto.request;

import java.util.List;

public record ReviewCreateRequestDto(
    Long revieweeId,
    List<Long> reviewContentId,
    String content
) {
}
