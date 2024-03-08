package org.prgms.locomocoserver.mogakkos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.prgms.locomocoserver.mogakkos.domain.likes.MogakkoLike;

public record MogakkoLikeDto(
        @Schema(description = "좋아요 상태 변경된 모각코 id") Long mogakkoId,
        @Schema(description = "좋아요 상태 변경을 요청한 사용자 id") Long userId,
        @Schema(description = "모각코 좋아요 여부") boolean isLike,
        @Schema(description = "모각코 전체 좋아요 개수") int totalLikeCount
) {
    public static MogakkoLikeDto create(MogakkoLike like, int totalLikeCount) {
        return new MogakkoLikeDto(like.getMogakko().getId(), like.getUser().getId(), like.isLike(), totalLikeCount);
    }
}
