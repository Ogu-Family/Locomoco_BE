package org.prgms.locomocoserver.categories.dto.response;

import java.util.List;
import org.prgms.locomocoserver.categories.dto.CategoriesWithTagsDto;

public record AllCategoriesWithTagsResponseDto(List<CategoriesWithTagsDto> data) {

}
