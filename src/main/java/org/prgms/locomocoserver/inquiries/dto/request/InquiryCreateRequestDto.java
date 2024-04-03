package org.prgms.locomocoserver.inquiries.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record InquiryCreateRequestDto(@Schema(description = "작성자 id", example = "1") Long userId,
                                      @Schema(description = "모각코 id", example = "313415") Long mogakkoId,
                                      @Schema(description = "문의 내용", example = "영감... 미안해요.. 다시는 안뽑기로 했는데... 울어라, 지옥참마도!") String content) {

}
