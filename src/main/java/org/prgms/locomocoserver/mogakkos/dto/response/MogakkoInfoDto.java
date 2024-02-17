package org.prgms.locomocoserver.mogakkos.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.tags.dto.TagsInfoDto;

public record MogakkoInfoDto(Long mogakkoId,
                             String title,
                             String content,
                             LocalDateTime startTime,
                             LocalDateTime endTime,
                             LocalDateTime deadline,
                             LocalDateTime createdAt,
                             String location,
                             int maxParticipants,
                             int likeCount,
                             List<Long> tagIds) {

    public static MogakkoInfoDto create(Mogakko mogakko, List<Long> tagIds) {
        return new MogakkoInfoDto(mogakko.getId(),
            mogakko.getTitle(),
            mogakko.getContent(),
            mogakko.getStartTime(),
            mogakko.getEndTime(),
            mogakko.getDeadline(),
            mogakko.getCreatedAt(),
            mogakko.getLocation(),
            mogakko.getMaxParticipants(),
            mogakko.getLikeCount(),
            tagIds);
    }
}
