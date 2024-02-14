package org.prgms.locomocoserver.categories.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.prgms.locomocoserver.global.common.InputType;
import org.prgms.locomocoserver.tags.dto.TagsInfoDto;

public record CategoriesWithTagsDto(@JsonProperty("category_id") Long categoryId,
                                    @JsonProperty("category_name") String categoryName,
                                    @JsonProperty("input_type") InputType inputType,
                                    List<TagsInfoDto> tags) {

}
