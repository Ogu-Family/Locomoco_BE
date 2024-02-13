package org.prgms.locomocoserver.mogakkos.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.MGCType;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;

public record MogakkoCreateRequestDto(String title,
                                      String location,
                                      MGCType mgcType,
                                      LocalDateTime startTime,
                                      LocalDateTime endTime,
                                      LocalDateTime deadline,
                                      Integer maxParticipants,
                                      String content,
                                      List<SelectedTagsDto> tags) {

    public Mogakko toMogakkoWithoutTags() {
        return Mogakko.builder()
            .title(title)
            .content(content)
            .mgcType(mgcType)
            .startTime(startTime)
            .endTime(endTime)
            .deadline(deadline)
            .maxParticipants(maxParticipants)
            .build();
    }
}
