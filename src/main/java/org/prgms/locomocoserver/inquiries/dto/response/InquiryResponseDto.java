package org.prgms.locomocoserver.inquiries.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

public record InquiryResponseDto(@Schema(description = "작성자 id") Long userId,
                                 @Schema(description = "프로필 사진 저장 위치") String profilePicture,
                                 @Schema(description = "작성자 닉네임") String nickname,
                                 @Schema(description = "생성 시간") LocalDateTime createdAt,
                                 @Schema(description = "수정 시간") LocalDateTime updatedAt,
                                 @Schema(description = "작성 내용") String content,
                                 @Schema(description = "대댓글 id 목록") List<Long> replies) {

}
