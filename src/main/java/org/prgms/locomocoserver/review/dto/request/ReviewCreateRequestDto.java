package org.prgms.locomocoserver.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ReviewCreateRequestDto(
        @Schema(description = "리뷰 대상자 id", example = "3")
        Long revieweeId,
        @Schema(description = "별점", example = "4")
        int score,
        @Schema(description = "리뷰 체크박스 id 리스트", example = "[1, 3, 5]")
        List<Long> reviewContentId,
        @Schema(description = "리뷰 내용", example = "와우")
        String content
) {
}
