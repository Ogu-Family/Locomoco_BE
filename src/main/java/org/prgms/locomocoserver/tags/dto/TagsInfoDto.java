package org.prgms.locomocoserver.tags.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TagsInfoDto(@JsonProperty("tag_id") Long tagId,
                          @JsonProperty("tag_name") String tagName) {

}
