package org.prgms.locomocoserver.mogakkos.dto;

import org.prgms.locomocoserver.mogakkos.domain.midpoint.Midpoint;
import org.prgms.locomocoserver.mogakkos.domain.vo.AddressInfo;

public record Place (String name,
                     String address,
                     double longitude,
                     double latitude,
                     int distance) {

    public Midpoint toMidpoint() {
        return Midpoint.builder()
            .longitude(this.longitude)
            .latitude(this.latitude)
            .addressInfo(AddressInfo.builder().address(address).build())
            .placeName(name)
            .build();
    }
}
