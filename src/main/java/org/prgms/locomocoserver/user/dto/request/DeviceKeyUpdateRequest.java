package org.prgms.locomocoserver.user.dto.request;

public record DeviceKeyUpdateRequest(
        String deviceType,
        String token
) {
}
