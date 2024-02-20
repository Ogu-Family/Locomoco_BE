package org.prgms.locomocoserver.inquiries.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record InquiryDeleteRequestDto(@Schema(description = "작성자 id", example = "1") Long userId) {

}
