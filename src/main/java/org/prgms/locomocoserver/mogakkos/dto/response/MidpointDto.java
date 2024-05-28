package org.prgms.locomocoserver.mogakkos.dto.response;

import org.prgms.locomocoserver.mogakkos.domain.midpoint.Midpoint;

public record MidpointDto(double longitude,
                          double latitude,
                          String Address,
                          String city) {

    public static MidpointDto from(Midpoint midpoint) {
        return new MidpointDto(midpoint.getLongitude(),
            midpoint.getLatitude(),
            midpoint.getAddress(),
            midpoint.getCity());
    }
}
