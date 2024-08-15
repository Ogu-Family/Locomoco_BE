package org.prgms.locomocoserver.mogakkos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocation;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocation.MogakkoLocationBuilder;
import org.prgms.locomocoserver.mogakkos.domain.vo.AddressInfo;
import org.prgms.locomocoserver.mogakkos.dto.LocationInfoDto;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;

public record MogakkoCreateRequestDto(@Schema(description = "작성자 id", example = "1") Long creatorId,
                                      @Schema(description = "모각코 글 제목", example = "모여서 각자 코딩") String title,
                                      @Schema(description = "모각코 장소") LocationInfoDto location,
                                      @Schema(description = "모각코 시작 시간") LocalDateTime startTime,
                                      @Schema(description = "모각코 종료 시간") LocalDateTime endTime,
                                      @Schema(description = "모각코 모집 데드라인 시간") LocalDateTime deadline,
                                      @Schema(description = "최대 참여자 수", example = "4") Integer maxParticipants,
                                      @Schema(description = "모각코 글 내용", example = "모각코 모여~") String content,
                                      @Schema(description = "선택된 태그 id 모음", example = "[1, 2, 3]") List<Long> tags) {

    public Mogakko toDefaultMogakko() {
        return Mogakko.builder()
            .title(title)
            .content(content)
            .startTime(startTime)
            .endTime(endTime)
            .deadline(deadline)
            .likeCount(0)
            .views(0)
            .maxParticipants(maxParticipants != null ? maxParticipants : Mogakko.DEFAULT_MAX_PARTICIPANTS)
            .build();
    }

    public MogakkoLocation toLocation() {
        MogakkoLocationBuilder builder = MogakkoLocation.builder();

        if (location == null) {
            return builder.build();
        }

        AddressInfo addressInfo = AddressInfo.builder().address(location.address())
            .city(location.city()).hCity(location.hCity()).build();

        return builder
            .addressInfo(addressInfo)
            .latitude(location.latitude())
            .longitude(location.longitude())
            .build();
    }
}
