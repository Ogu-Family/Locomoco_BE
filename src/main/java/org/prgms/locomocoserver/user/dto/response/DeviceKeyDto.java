package org.prgms.locomocoserver.user.dto.response;

import org.prgms.locomocoserver.user.domain.mongo.DeviceKeyMongo;

public record DeviceKeyDto(
        String phone,
        String pad,
        String desktop
) {
    public static DeviceKeyDto of(DeviceKeyMongo deviceKeyMongo) {
        return new DeviceKeyDto(
                deviceKeyMongo.getPhone(),
                deviceKeyMongo.getPad(),
                deviceKeyMongo.getDesktop()
        );
    }
}
