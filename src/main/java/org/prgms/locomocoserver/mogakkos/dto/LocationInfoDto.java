package org.prgms.locomocoserver.mogakkos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocation;
import org.prgms.locomocoserver.mogakkos.domain.vo.AddressInfo;

public record LocationInfoDto(@Schema(description = "주소", example = "경기도 부천시 소사로 114번길 5") String address,
                              @Schema(description = "위도", example = "31.42958390") Double latitude,
                              @Schema(description = "경도", example = "123.123456789") Double longitude,
                              @Schema(description = "법정동 정보", example = "경기도 부천시 소사구 소사본1동") String city,
                              @Schema(description = "행정동 정보", example = "경기도 부천시 소사구 소사본동") String hCity) {

    public static LocationInfoDto create(MogakkoLocation mogakkoLocation) {
        if (mogakkoLocation == null) {
            return null;
        }

        AddressInfo locationAddressInfo = mogakkoLocation.getAddressInfo();

        return new LocationInfoDto(locationAddressInfo.getAddress(),
            mogakkoLocation.getLatitude(),
            mogakkoLocation.getLongitude(),
            locationAddressInfo.getCity(),
            locationAddressInfo.getHCity());
    }
}
