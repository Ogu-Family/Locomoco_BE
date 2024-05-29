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
    public Midpoint(double latitude, double longitude, String address, String city, Long id, String placeName,
        Mogakko mogakko) {
        super(latitude, longitude, address, city);
        this.id = id;
        this.placeName = placeName;
        this.mogakko = mogakko;
    }

    public void update(double latitude, double longitude, String address, String placeName, Mogakko mogakko) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.placeName = placeName;
        this.mogakko = mogakko;
    }
}
