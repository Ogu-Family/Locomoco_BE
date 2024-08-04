package org.prgms.locomocoserver.mogakkos.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddressInfo {

    @Column(name = "address")
    protected String address;

    @Column(name = "city")
    protected String city;

    @Column(name = "h_city")
    protected String hCity;

    @Builder
    public AddressInfo(String address, String city, String hCity) {
        this.address = address;
        this.city = city;
        this.hCity = hCity;
    }
}
