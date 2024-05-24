package org.prgms.locomocoserver.mogakkos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.prgms.locomocoserver.global.common.BaseEntity;

@Getter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Location extends BaseEntity {
    @Column(name = "latitude", columnDefinition = "decimal(13, 10)", nullable = false)
    protected double latitude;

    @Column(name = "longitude", columnDefinition = "decimal(13, 10)", nullable = false)
    protected double longitude;

    @Column(name = "address")
    protected String address;

    @Column(name = "city")
    protected String city;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogakko_id")
    protected Mogakko mogakko;

    protected Location(double latitude, double longitude, String address, String city,
        Mogakko mogakko) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.city = city;
        this.mogakko = mogakko;
    }
}
