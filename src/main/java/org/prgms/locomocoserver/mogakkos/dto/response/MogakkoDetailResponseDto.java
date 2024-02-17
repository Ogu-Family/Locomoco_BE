package org.prgms.locomocoserver.mogakkos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.prgms.locomocoserver.user.dto.response.UserBriefInfoDto;

public record MogakkoDetailResponseDto(@Schema(description = "생성자 정보") UserBriefInfoDto creatorInfo,
                                       @Schema(description = "참여자 목록") List<MogakkoParticipantDto> participants,
                                       @Schema(description = "모각코 정보") MogakkoInfoDto MogakkoInfo) { // TODO: 문의 추가

}
