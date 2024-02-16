package org.prgms.locomocoserver.mogakkos.dto.response;

import java.util.List;
import org.prgms.locomocoserver.user.dto.response.UserBriefInfoDto;

public record MogakkoDetailResponseDto(UserBriefInfoDto userInfo,
                                       List<MogakkoParticipantDto> participants,
                                       MogakkoInfoDto info) {

}
