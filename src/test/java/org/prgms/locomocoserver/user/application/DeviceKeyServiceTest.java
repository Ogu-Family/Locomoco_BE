package org.prgms.locomocoserver.user.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.user.domain.mongo.DeviceKey;
import org.prgms.locomocoserver.user.domain.mongo.DeviceKeyMongoRepository;
import org.prgms.locomocoserver.user.dto.request.DeviceKeyUpdateRequest;
import org.prgms.locomocoserver.user.dto.response.DeviceKeyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DeviceKeyServiceTest {

    @Autowired
    private DeviceKeyMongoRepository deviceKeyMongoRepository;

    @Autowired
    private DeviceKeyService deviceKeyService;

    @BeforeEach
    void setUp() {
        deviceKeyMongoRepository.deleteAll();
    }

    @Test
    @DisplayName("userId에 맞는 디바이스 토큰을 저장할 수 있다.")
    void saveDeviceKey() {
        // given

        // when
        DeviceKeyDto deviceKey = deviceKeyService.saveDeviceKey(String.valueOf(2L));

        // then
        assertThat(deviceKey.pad()).isEqualTo(null);
        assertThat(deviceKey.phone()).isEqualTo(null);
        assertThat(deviceKey.desktop()).isEqualTo(null);
    }

    @Test
    @DisplayName("userId로 디바이스 토큰을 조회할 수 있다.")
    void getByUserId() {
        // given
        DeviceKey deviceKeyMongo = deviceKeyMongoRepository.save(DeviceKey.builder()
                .userId("2").desktop("desktop test").pad("pad test").phone("phone test").build());

        // when
        DeviceKeyDto deviceKey = deviceKeyService.getByUserId("2");

        // then
        assertThat(deviceKey.desktop()).isEqualTo(deviceKeyMongo.getDesktop());
        assertThat(deviceKey.pad()).isEqualTo(deviceKeyMongo.getPad());
        assertThat(deviceKey.phone()).isEqualTo(deviceKeyMongo.getPhone());
    }

    @Test
    @DisplayName("userId, 디바이스 타입으로 token 값을 업데이트 할 수 있다.")
    void updateDeviceKey() {
        // given
        DeviceKey deviceKey = deviceKeyMongoRepository.save(DeviceKey.builder()
                .userId("2").desktop("desktop test").pad("pad test").phone("phone test").build());
        DeviceKeyUpdateRequest request = new DeviceKeyUpdateRequest("phone", "update");

        // when
        DeviceKeyDto deviceKeyDto = deviceKeyService.updateDeviceKey("2", request);

        // then
        assertThat(deviceKeyDto.phone()).isEqualTo("update");
    }
}