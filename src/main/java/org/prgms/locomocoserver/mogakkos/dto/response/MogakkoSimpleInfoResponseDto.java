package org.prgms.locomocoserver.mogakkos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.prgms.locomocoserver.location.dto.LocationInfoDto;

public record MogakkoSimpleInfoResponseDto(@Schema(description = "제목") String title,
                                           @Schema(description = "조회 수") int views,
                                           @Schema(description = "좋아요(찜) 수") int likeCount,
                                           @Schema(description = "최대 참여자 수") int maxParticipants,
                                           @Schema(description = "현재 참여자 수") int curParticipants,
                                           @Schema(description = "장소 정보") LocationInfoDto location,
                                           @Schema(description = "태그 id 목록") List<Long> tags) {

}
