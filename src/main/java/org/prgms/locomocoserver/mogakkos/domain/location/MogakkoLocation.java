package org.prgms.locomocoserver.mogakkos.domain.location;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.mogakkos.domain.Location;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.vo.AddressInfo;

@Entity
@Getter
@Table(name = "locations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MogakkoLocation extends Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogakko_id")
    protected Mogakko mogakko;

    @Builder
    public MogakkoLocation(Double latitude, Double longitude, AddressInfo addressInfo, Mogakko mogakko, Long id) {
        super(latitude, longitude, addressInfo);
        this.id = id;
        this.mogakko = mogakko;
    }

    public void updateInfo(Double latitude, Double longitude, AddressInfo addressInfo) {
        this.latitude = latitude == null ? this.latitude : latitude;
        this.longitude = longitude == null ? this.longitude : longitude;
        this.addressInfo = addressInfo == null ? this.addressInfo : this.addressInfo.update(addressInfo);
    }

    public void updateMogakko(Mogakko mogakko) {
        this.mogakko = mogakko;
    }
}
