package org.prgms.locomocoserver.user.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.user.domain.mongo.DeviceKeyMongo;
import org.prgms.locomocoserver.user.domain.mongo.DeviceKeyMongoRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceKeyService {

    private final DeviceKeyMongoRepository deviceKeyMongoRepository;

    public void saveDeviceKey(String userId) {
        deviceKeyMongoRepository.save(DeviceKeyMongo.builder().userId(userId).build());
    }
}
