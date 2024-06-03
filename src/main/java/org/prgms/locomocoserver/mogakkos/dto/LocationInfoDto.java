package org.prgms.locomocoserver.mogakkos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocation;

public record LocationInfoDto(@Schema(description = "주소", example = "경기도 부천시 소사로 114번길 5") String address,
                              @Schema(description = "위도", example = "31.42958390") Double latitude,
                              @Schema(description = "경도", example = "123.123456789") Double longitude,
                              @Schema(description = "동/읍/면", example = "소사본동") String city) {

    public static LocationInfoDto create(MogakkoLocation mogakkoLocation) {
        return new LocationInfoDto(mogakkoLocation.getAddress(),
            mogakkoLocation.getLatitude(),
            mogakkoLocation.getLongitude(),
            mogakkoLocation.getCity());
    }
}
