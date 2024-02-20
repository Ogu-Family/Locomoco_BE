package org.prgms.locomocoserver.mogakkos.dto.response;

import java.util.List;
import org.prgms.locomocoserver.location.dto.LocationInfoDto;

public record MogakkoSimpleInfoResponseDto(String title,
                                           int views,
                                           int likeCount,
                                           int maxParticipants,
                                           int curParticipants,
                                           LocationInfoDto location,
                                           List<Long> tags) {

}
