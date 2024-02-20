package org.prgms.locomocoserver.inquiries.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record InquiryUpdateRequestDto(@Schema(description = "작성자 id", example = "1") Long userId,
                                      @Schema(description = "문의 id", example = "1") Long inquiryId,
                                      @Schema(description = "수정 내용", example = "할멈... 미안해요.. 다시는 안뽑기로 했는데... 울어라, 지옥참마도!") String content) {

}
