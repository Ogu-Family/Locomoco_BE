package org.prgms.locomocoserver.blacklist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record BlacklistRequestDto(@Schema(description = "블랙리스트로 등록할 유저 id", example = "1") Long blockedId) {

}
