package org.prgms.locomocoserver.user.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.user.domain.mongo.DeviceKey;
import org.prgms.locomocoserver.user.domain.mongo.DeviceKeyMongoRepository;
import org.prgms.locomocoserver.user.dto.request.DeviceKeyUpdateRequest;
import org.prgms.locomocoserver.user.dto.response.DeviceKeyDto;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeviceKeyService {

    private final DeviceKeyMongoRepository deviceKeyMongoRepository;

    public DeviceKeyDto saveDeviceKey(String userId) {
        DeviceKey deviceKey = deviceKeyMongoRepository.save(DeviceKey.builder().userId(userId).build());
        return DeviceKeyDto.of(deviceKey);
    }

    public DeviceKeyDto getByUserId(String userId) {
        return DeviceKeyDto.of(deviceKeyMongoRepository.findByUserId(userId).orElseThrow(() -> new UserException(UserErrorType.USER_NOT_FOUND)));
    }

    @Transactional
    public DeviceKeyDto updateDeviceKey(String userId, DeviceKeyUpdateRequest request) {
        DeviceKey deviceKey = deviceKeyMongoRepository.findByUserId(userId).orElseThrow(() -> new UserException(UserErrorType.USER_NOT_FOUND));
        switch (request.deviceType()) {
            case "phone":
                deviceKey.updatePhone(request.token());
                break;
            case "pad":
                deviceKey.updatePad(request.token());
                break;
            case "desktop":
                deviceKey.updateDesktop(request.token());
                break;
            default:
                throw new IllegalArgumentException("Device Type Error: " + request.deviceType());
        }
        return DeviceKeyDto.of(deviceKey);
    }
}
