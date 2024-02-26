package org.prgms.locomocoserver.inquiries.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.prgms.locomocoserver.inquiries.domain.Inquiry;

public record InquiryUpdateResponseDto(@Schema(description = "수정된 문의 id", example = "312") Long id) {

    public static InquiryUpdateResponseDto create(Inquiry inquiry) {
        return new InquiryUpdateResponseDto(inquiry.getId());
    }
}
