package org.prgms.locomocoserver.mogakkos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record MogakkoFilterRequestDto(@Schema(description = "주소 검색 값", example = "고척동") String address,
                                      @Schema(description = "지도 상 현재 위도", example = "27.414192012") Double altitude,
                                      @Schema(description = "지도 상 현재 경도", example = "74.278827613") Double longitude,
                                      @Schema(description = "필터링 태그 id 목록") List<Long> tags) {

}
