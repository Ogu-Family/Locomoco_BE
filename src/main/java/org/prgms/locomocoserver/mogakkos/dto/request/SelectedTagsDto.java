package org.prgms.locomocoserver.mogakkos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record SelectedTagsDto(@Schema(description = "카테고리 id") Long categoryId,
                              @Schema(description = "태그 id 리스트") List<Long> tagIds) {

}
