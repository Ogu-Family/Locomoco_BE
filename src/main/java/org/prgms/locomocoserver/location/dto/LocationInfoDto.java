package org.prgms.locomocoserver.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.prgms.locomocoserver.location.domain.Location;

public record LocationInfoDto(@Schema(description = "주소", example = "경기도 부천시 소사로 114번길 5") String address,
                              @Schema(description = "위도", example = "31.42958390") Double latitude,
                              @Schema(description = "경도", example = "123.123456789") Double longitude,
                              @Schema(description = "동", example = "고척동") String city) {

    public static LocationInfoDto create(Location location) {
        return new LocationInfoDto(location.getAddress(),
            location.getLatitude(),
            location.getLongitude(),
            location.getCity());
    }
}
