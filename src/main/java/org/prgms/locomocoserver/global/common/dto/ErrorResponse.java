package org.prgms.locomocoserver.global.common.dto;

public record ErrorResponse(
        Integer code,
        String message
) {
}
