package org.prgms.locomocoserver.mogakkos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MogakkoCreateResponseDto(@Schema(description = "생성된 모각코 id") Long id) {

}
