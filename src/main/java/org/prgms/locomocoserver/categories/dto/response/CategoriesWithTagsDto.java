package org.prgms.locomocoserver.categories.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.prgms.locomocoserver.global.common.InputType;
import org.prgms.locomocoserver.tags.dto.TagsInfoDto;

public record CategoriesWithTagsDto(@Schema(description = "카테고리 id") @JsonProperty("category_id") Long categoryId,
                                    @Schema(description = "카테고리 이름") @JsonProperty("category_name") String categoryName,
                                    @Schema(description = "입력받는 폼 타입", example = "DROPDOWN") @JsonProperty("input_type") InputType inputType,
                                    @Schema(description = "해당 카테고리에 포함된 태그들") List<TagsInfoDto> tags) {

}
