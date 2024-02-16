package org.prgms.locomocoserver.mogakkos.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import org.prgms.locomocoserver.categories.dto.response.CategoriesWithTagsDto;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;

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
                             List<CategoriesWithTagsDto> tags) {

    public static MogakkoInfoDto create(Mogakko mogakko, List<CategoriesWithTagsDto> tags) {
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
            tags);
    }
}
