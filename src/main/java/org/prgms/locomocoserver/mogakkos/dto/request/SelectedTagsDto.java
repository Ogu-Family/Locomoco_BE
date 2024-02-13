package org.prgms.locomocoserver.mogakkos.dto.request;

import java.util.List;

public record SelectedTagsDto(Long categoryId,
                              List<Long> tagIds) {

}
