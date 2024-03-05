package org.prgms.locomocoserver.mogakkos.dto.response;

import org.prgms.locomocoserver.mogakkos.domain.likes.Like;

public record MogakkoLikeDto(
        Long likeId,
        Long mogakkoId,
        Long userId,
        boolean isLike,
        int totalLikeCount
) {
    public static MogakkoLikeDto create(Like like, int totalLikeCount) {
        return new MogakkoLikeDto(like.getId(), like.getMogakko().getId(), like.getUser().getId(), like.isLike(), totalLikeCount);
    }
}
