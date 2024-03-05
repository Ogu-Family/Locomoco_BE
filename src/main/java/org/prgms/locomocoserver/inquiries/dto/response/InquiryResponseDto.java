package org.prgms.locomocoserver.inquiries.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

import org.prgms.locomocoserver.image.dto.ImageDto;
import org.prgms.locomocoserver.inquiries.domain.Inquiry;
import org.prgms.locomocoserver.user.domain.User;

public record InquiryResponseDto(@Schema(description = "문의 id") Long inquiryId,
                                 @Schema(description = "작성자 id") Long userId,
                                 @Schema(description = "모각코 id") Long mogakkoId,
                                 @Schema(description = "프로필 사진 저장 위치") ImageDto profileImage,
                                 @Schema(description = "작성자 닉네임") String nickname,
                                 @Schema(description = "생성 시간") LocalDateTime createdAt,
                                 @Schema(description = "수정 시간") LocalDateTime updatedAt,
                                 @Schema(description = "작성 내용") String content) {
    public static InquiryResponseDto create(Inquiry inquiry, User user) {
        return new InquiryResponseDto(inquiry.getId(),
            user.getId(),
            inquiry.getMogakko().getId(),
            ImageDto.of(user.getProfileImage()),
            user.getNickname(),
            inquiry.getCreatedAt(),
            inquiry.getUpdatedAt(),
            inquiry.getContent());
    }

}
