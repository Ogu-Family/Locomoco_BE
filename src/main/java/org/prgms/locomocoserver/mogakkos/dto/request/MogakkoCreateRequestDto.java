package org.prgms.locomocoserver.mogakkos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.MGCType;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;

public record MogakkoCreateRequestDto(@Schema(description = "모각코 글 제목") String title,
                                      @Schema(description = "모각코 장소") String location,
                                      @Schema(description = "모각코 타입") MGCType mgcType,
                                      @Schema(description = "모각코 시작 시간") LocalDateTime startTime,
                                      @Schema(description = "모각코 종료 시간") LocalDateTime endTime,
                                      @Schema(description = "모각코 모집 데드라인 시간") LocalDateTime deadline,
                                      @Schema(description = "최대 참여자 수") Integer maxParticipants,
                                      @Schema(description = "모각코 글 내용") String content,
                                      @Schema(description = "선택된 태그 id 모음") List<SelectedTagsDto> tags) {

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
