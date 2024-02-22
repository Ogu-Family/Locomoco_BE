package org.prgms.locomocoserver.mogakkos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record MogakkoFilterRequestDto(@Schema(description = "필터링 태그 id 목록") List<Long> tags) {

}
