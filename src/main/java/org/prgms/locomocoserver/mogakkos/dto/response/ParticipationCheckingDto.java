package org.prgms.locomocoserver.mogakkos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ParticipationCheckingDto(@Schema(description = "참여 여부") boolean isParticipated) {

}
