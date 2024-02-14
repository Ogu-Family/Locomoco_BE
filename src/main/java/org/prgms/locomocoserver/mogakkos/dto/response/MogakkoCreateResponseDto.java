package org.prgms.locomocoserver.mogakkos.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MogakkoCreateResponseDto(@JsonProperty("mogakko_id") Long id) {

}
