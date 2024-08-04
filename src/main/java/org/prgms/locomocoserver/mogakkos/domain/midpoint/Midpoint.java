package org.prgms.locomocoserver.mogakkos.domain.midpoint;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.mogakkos.domain.Location;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.vo.AddressInfo;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Midpoint extends Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_name")
    private String placeName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogakko_id")
    protected Mogakko mogakko;

    @Builder
    public Midpoint(double latitude, double longitude, AddressInfo addressInfo, Long id, String placeName,
        Mogakko mogakko) {
        super(latitude, longitude, addressInfo);
        this.id = id;
        this.placeName = placeName;
        this.mogakko = mogakko;
    }

    public void updateInfo(Double latitude, Double longitude, AddressInfo addressInfo, String placeName, Mogakko mogakko) {
        this.latitude = latitude == null ? this.latitude : latitude;
        this.longitude = longitude == null ? this.longitude : longitude;
        this.addressInfo = addressInfo == null ? this.addressInfo : this.addressInfo.update(addressInfo);
        this.placeName = placeName == null ? this.placeName : placeName;
        this.mogakko = mogakko == null ? this.mogakko : mogakko;
        this.updateUpdatedAt();
    }
}
