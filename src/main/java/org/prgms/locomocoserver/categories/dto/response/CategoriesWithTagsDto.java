package org.prgms.locomocoserver.categories.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.prgms.locomocoserver.tags.dto.TagsInfoDto;

public record CategoriesWithTagsDto(@Schema(description = "카테고리 id", example = "1") @JsonProperty("category_id") Long categoryId,
                                    @Schema(description = "카테고리 이름", example = "개발 언어") @JsonProperty("category_name") String categoryName,
                                    @Schema(description = "해당 카테고리에 포함된 태그들") List<TagsInfoDto> tags) {

}
