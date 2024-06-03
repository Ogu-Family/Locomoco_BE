package org.prgms.locomocoserver.mogakkos.domain.participants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;

@Entity
@Getter
@Table(name = "participants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latitude", columnDefinition = "decimal(13, 10)")
    private Double latitude;

    @Column(name = "longitude", columnDefinition = "decimal(13, 10)")
    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogakko_id")
    private Mogakko mogakko;

    @Builder
    public Participant(Double latitude, Double longitude, User user, Mogakko mogakko) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
        this.mogakko = mogakko;
    }

    public void updateMogakko(Mogakko mogakko) {
        if (Objects.nonNull(mogakko)) {
            mogakko.getParticipants().remove(this);
        }

        this.mogakko = mogakko;
        mogakko.getParticipants().add(this);
    }

    public void updateLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
