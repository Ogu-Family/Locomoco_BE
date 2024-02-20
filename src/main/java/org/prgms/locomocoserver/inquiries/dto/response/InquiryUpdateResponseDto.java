package org.prgms.locomocoserver.inquiries.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record InquiryUpdateResponseDto(@Schema(description = "수정된 문의 id", example = "312") Long id) {

}
