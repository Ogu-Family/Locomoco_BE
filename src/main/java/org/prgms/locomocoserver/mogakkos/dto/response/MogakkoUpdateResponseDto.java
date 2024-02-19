package org.prgms.locomocoserver.mogakkos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MogakkoUpdateResponseDto(@Schema(description = "수정된 모각코 id") Long id) {

}
