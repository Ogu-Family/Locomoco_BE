package org.prgms.locomocoserver.blacklist.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.user.domain.User;

@Entity
@Getter
@Table(name = "blacklists")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id")
    private User blockUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id")
    private User blockedUser;

    @Builder
    public Blacklist(User blockUser, User blockedUser) {
        this.blockUser = blockUser;
        this.blockedUser = blockedUser;
    }

    public void updateBlockUser(User user) {
        this.blockUser = user;
    }

    public void updateBlockedUser(User user) {
        this.blockedUser = user;
    }
}
