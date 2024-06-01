package org.prgms.locomocoserver.mogakkos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ParticipationRequestDto(@Schema(description = "참여하고자 하는 유저 id", example = "1") Long userId,
                                      @Schema(description = "참여하고자 하는 유저의 출발 경도", example = "126.97335352452") Double longitude,
                                      @Schema(description = "참여하고자 하는 유저의 출발 위도", example = "26.83783263") Double latitude) {

    public ParticipationRequestDto(Long userId) {
        this(userId, null, null);
    }
}
