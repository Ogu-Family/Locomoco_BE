package org.prgms.locomocoserver.user.dto.request;

public record DeviceKeyUpdateRequest(
        String id, // 프론트 타입 이유로 userId 값 추가
        String deviceType,
        String token
) {
}
