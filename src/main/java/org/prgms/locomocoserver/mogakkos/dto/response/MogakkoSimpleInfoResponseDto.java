package org.prgms.locomocoserver.mogakkos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import org.prgms.locomocoserver.location.domain.Location;
import org.prgms.locomocoserver.location.dto.LocationInfoDto;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;

public record MogakkoSimpleInfoResponseDto(@Schema(description = "모각코 id") Long id,
                                           @Schema(description = "제목") String title,
                                           @Schema(description = "조회 수") long views,
                                           @Schema(description = "좋아요(찜) 수") int likeCount,
                                           @Schema(description = "생성 시간") LocalDateTime createdAt,
                                           @Schema(description = "수정 시간") LocalDateTime updatedAt,
                                           @Schema(description = "최대 참여자 수") int maxParticipants,
                                           @Schema(description = "현재 참여자 수") int curParticipants,
                                           @Schema(description = "장소 정보") LocationInfoDto location,
                                           @Schema(description = "태그 id 목록") List<Long> tags) {

    public static MogakkoSimpleInfoResponseDto create(Mogakko mogakko, Location location) {
        return new MogakkoSimpleInfoResponseDto(mogakko.getId(),
            mogakko.getTitle(),
            mogakko.getViews(),
            mogakko.getLikeCount(),
            mogakko.getCreatedAt(),
            mogakko.getUpdatedAt(),
            mogakko.getMaxParticipants(),
            mogakko.getParticipants().size(),
            LocationInfoDto.create(location),
            mogakko.getMogakkoTags().stream().map(mogakkoTag -> mogakkoTag.getTag().getId())
                .toList());
    }
}
