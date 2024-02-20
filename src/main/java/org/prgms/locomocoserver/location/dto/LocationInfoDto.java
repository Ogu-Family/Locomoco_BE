package org.prgms.locomocoserver.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LocationInfoDto(@Schema(description = "주소") String address,
                              @Schema(description = "위도") Double latitude,
                              @Schema(description = "경도") Double longitude) {

}
