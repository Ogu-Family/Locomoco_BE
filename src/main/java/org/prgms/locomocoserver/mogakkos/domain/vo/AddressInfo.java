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
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "h_city")
    private String hCity;

    @Builder
    public AddressInfo(String address, String city, String hCity) {
        this.address = address;
        this.city = city;
        this.hCity = hCity;
    }

    public AddressInfo update(AddressInfo addressInfo) {
        String updateAddress = addressInfo.address == null ? this.address : addressInfo.address;
        String updateCity = addressInfo.city == null ? this.city : addressInfo.city;
        String updateHCity = addressInfo.hCity == null ? this.hCity : addressInfo.hCity;

        return new AddressInfo(updateAddress, updateCity, updateHCity);
    }
}
