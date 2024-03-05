package org.prgms.locomocoserver.mogakkos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import org.prgms.locomocoserver.location.dto.LocationInfoDto;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;

public record MogakkoInfoDto(@Schema(description = "모각코 id", example = "1") Long mogakkoId,
                             @Schema(description = "제목", example = "내일 부천에서 모각코 하실 분~") String title,
                             @Schema(description = "내용", example = "14시쯤에 모각코 하실 분 계신가용?") String content,
                             @Schema(description = "조회 수", example = "2401") long views,
                             @Schema(description = "시작 시간") LocalDateTime startTime,
                             @Schema(description = "끝나는 시간") LocalDateTime endTime,
                             @Schema(description = "참여 신청 기간") LocalDateTime deadline,
                             @Schema(description = "생성 시간") LocalDateTime createdAt,
                             @Schema(description = "장소") LocationInfoDto location,
                             @Schema(description = "최대 참여자 수", example = "4") int maxParticipants,
                             @Schema(description = "좋아요(찜) 수", example = "4812") int likeCount,
                             @Schema(description = "태그 id 목록", example = "[ 1, 2, 3 ]") List<Long> tagIds) {

    public static MogakkoInfoDto create(Mogakko mogakko, LocationInfoDto location, List<Long> tagIds) {
        return new MogakkoInfoDto(mogakko.getId(),
            mogakko.getTitle(),
            mogakko.getContent(),
            mogakko.getViews(),
            mogakko.getStartTime(),
            mogakko.getEndTime(),
            mogakko.getDeadline(),
            mogakko.getCreatedAt(),
            location,
            mogakko.getMaxParticipants(),
            mogakko.getLikeCount(),
            tagIds);
    }
}
