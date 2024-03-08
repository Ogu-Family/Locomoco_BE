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
@Table(name = "mogakko_likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MogakkoLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogakko_id")
    private Mogakko mogakko;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_like")
    private boolean isLike;

    @Builder
    public MogakkoLike(Mogakko mogakko, User user, boolean isLike) {
        this.mogakko = mogakko;
        this.user = user;
        this.isLike = isLike;
    }

    public void updateLike() {
        isLike = !isLike;
    }
}
