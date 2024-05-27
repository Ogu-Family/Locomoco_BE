package org.prgms.locomocoserver.user.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.user.domain.mongo.DeviceKeyMongo;
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

    public void saveDeviceKey(String userId) {
        deviceKeyMongoRepository.save(DeviceKeyMongo.builder().userId(userId).build());
    }

    public DeviceKeyDto getByUserId(String userId) {
        return DeviceKeyDto.of(deviceKeyMongoRepository.findByUserId(userId).orElseThrow(() -> new UserException(UserErrorType.USER_NOT_FOUND)));
    }

    @Transactional
    public DeviceKeyDto updateDeviceKey(String userId, DeviceKeyUpdateRequest request) {
        DeviceKeyMongo deviceKeyMongo = deviceKeyMongoRepository.findByUserId(userId).orElseThrow(() -> new UserException(UserErrorType.USER_NOT_FOUND));
        switch (request.deviceType()) {
            case "phone":
                deviceKeyMongo.updatePhone(request.token());
                break;
            case "pad":
                deviceKeyMongo.updatePad(request.token());
                break;
            case "desktop":
                deviceKeyMongo.updateDesktop(request.token());
                break;
            default:
                throw new IllegalArgumentException("Device Type Error: " + request.deviceType());
        }
        return DeviceKeyDto.of(deviceKeyMongo);
    }
}
