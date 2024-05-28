package org.prgms.locomocoserver.mogakkos.dto;

import org.prgms.locomocoserver.mogakkos.domain.midpoint.Midpoint;

public record Place (String name,
                     String address,
                     double longitude,
                     double latitude,
                     int distance) {

    public Midpoint toMidpoint() {
        return Midpoint.builder()
            .longitude(this.longitude)
            .latitude(this.latitude)
            .address(address)
            .build();
    }
}
