package org.prgms.locomocoserver.mogakkos.domain.likes;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;

@Entity
@Getter
@Table(name = "likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Column(name = "mogakko_id")
    private Mogakko mogakko;

    @ManyToOne
    @Column(name = "user_id")
    private User user;

    private boolean isLike;

    @Builder
    public Like(Mogakko mogakko, User user, boolean isLike) {
        this.mogakko = mogakko;
        this.user = user;
        this.isLike = isLike;
    }
}
