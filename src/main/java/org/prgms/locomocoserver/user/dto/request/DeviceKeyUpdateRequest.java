package org.prgms.locomocoserver.user.dto.request;

public record DeviceKeyUpdateRequest(
        String id, // 프론트 타입 오류로 수정
        String deviceType,
        String token
) {
}
