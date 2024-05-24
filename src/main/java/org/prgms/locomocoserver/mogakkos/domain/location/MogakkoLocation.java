package org.prgms.locomocoserver.mogakkos.domain.location;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.mogakkos.domain.Location;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;

@Entity
@Getter
@Table(name = "locations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MogakkoLocation extends Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder
    public MogakkoLocation(double latitude, double longitude, String address, String city,
        Mogakko mogakko, Long id) {
        super(latitude, longitude, address, city, mogakko);
        this.id = id;
    }

    public void updateInfo(double latitude, double longitude, String address, String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.city = city;
    }

    public void updateMogakko(Mogakko mogakko) {
        this.mogakko = mogakko;
    }
}
