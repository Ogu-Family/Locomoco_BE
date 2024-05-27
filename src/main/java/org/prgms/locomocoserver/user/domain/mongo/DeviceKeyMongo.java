package org.prgms.locomocoserver.user.domain.mongo;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "device_keys")
public class DeviceKeyMongo {

    @Id
    private String id;
    private String userId;
    private String phone;
    private String pad;
    private String desktop;

    @Builder
    public DeviceKeyMongo(String userId, String phone, String pad, String desktop) {
        this.userId = userId;
        this.phone = phone;
        this.pad = pad;
        this.desktop = desktop;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

    public void updatePad(String pad) {
        this.pad = pad;
    }

    public void updateDesktop(String desktop) {
        this.desktop = desktop;
    }
}
