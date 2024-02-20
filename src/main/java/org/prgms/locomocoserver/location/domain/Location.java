package org.prgms.locomocoserver.location.domain;

import jakarta.persistence.Column;
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
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;

@Entity
@Getter
@Table(name = "locations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latitude", columnDefinition = "decimal(13, 10)", nullable = false)
    private double latitude;

    @Column(name = "longitude", columnDefinition = "decimal(13, 10)", nullable = false)
    private double longitude;

    @Column(name = "address")
    private String address;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogakko_id")
    private Mogakko mogakko;

    @Builder
    public Location(double latitude, double longitude, String address, Mogakko mogakko) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.mogakko = mogakko;
    }

    public void updateMogakko(Mogakko mogakko) {
        this.mogakko = mogakko;
    }
}
