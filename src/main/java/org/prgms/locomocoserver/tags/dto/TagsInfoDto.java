package org.prgms.locomocoserver.tags.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record TagsInfoDto(@Schema(description = "태그 id", example = "1") @JsonProperty("tag_id") Long tagId,
                          @Schema(description = "태그 이름", example = "Typescript") @JsonProperty("tag_name") String tagName) {

}
