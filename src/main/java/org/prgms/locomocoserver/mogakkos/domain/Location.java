package org.prgms.locomocoserver.mogakkos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.global.common.BaseEntity;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Location extends BaseEntity { // TODO: 밸류 타입화
    @Column(name = "latitude", columnDefinition = "decimal(13, 10)", nullable = false)
    protected double latitude;

    @Column(name = "longitude", columnDefinition = "decimal(13, 10)", nullable = false)
    protected double longitude;

    @Column(name = "address")
    protected String address;

    @Column(name = "city")
    protected String city;

    protected Location(double latitude, double longitude, String address, String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.city = city;
    }

    public double calDistance(Location l) { // km 단위
        double distance;
        final double RADIUS = 6371; // 지구 반지름(km)
        final double TO_RADIAN = Math.PI / 180d;

        double deltaLat = Math.abs(this.getLatitude() - l.getLatitude()) * TO_RADIAN;
        double deltaLng = Math.abs(this.getLongitude() - l.getLongitude()) * TO_RADIAN;

        double sinDeltaLat = Math.sin(deltaLat / 2);
        double sinDeltaLng = Math.sin(deltaLng / 2);
        double squareRoot = Math.sqrt(
            sinDeltaLat * sinDeltaLat +
                Math.cos(this.getLatitude() * TO_RADIAN) * Math.cos(l.getLatitude() * TO_RADIAN) * sinDeltaLng * sinDeltaLng);

        distance = 2 * RADIUS * Math.asin(squareRoot);

        return distance;
    }
}
