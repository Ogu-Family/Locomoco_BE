package org.prgms.locomocoserver.mogakkos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.prgms.locomocoserver.mogakkos.domain.midpoint.Midpoint;

public record MidpointDto(@Schema(description = "경도", example = "125.69348453") double longitude,
                          @Schema(description = "위도", example = "32.43257823") double latitude,
                          @Schema(description = "주소", example = "경기도 땡땡시 땡땡동 땡땡로 70") String address,
                          @Schema(description = "장소 이름", example = "로코모코 카페") String name) {

    public static MidpointDto from(Midpoint midpoint) {
        return new MidpointDto(midpoint.getLongitude(),
            midpoint.getLatitude(),
            midpoint.getAddress(),
            midpoint.getPlaceName());
    }
}
