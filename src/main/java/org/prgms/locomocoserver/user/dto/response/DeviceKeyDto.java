package org.prgms.locomocoserver.user.dto.response;

import org.prgms.locomocoserver.user.domain.mongo.DeviceKey;

public record DeviceKeyDto(
        String phone,
        String pad,
        String desktop
) {
    public static DeviceKeyDto of(DeviceKey deviceKey) {
        return new DeviceKeyDto(
                deviceKey.getPhone(),
                deviceKey.getPad(),
                deviceKey.getDesktop()
        );
    }
}
