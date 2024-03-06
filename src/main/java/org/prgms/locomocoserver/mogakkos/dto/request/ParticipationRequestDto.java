package org.prgms.locomocoserver.mogakkos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ParticipationRequestDto(@Schema(description = "참여하고자 하는 유저 id", example = "1") Long userId) {

}
