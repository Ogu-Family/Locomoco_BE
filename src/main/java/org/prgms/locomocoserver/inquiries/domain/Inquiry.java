package org.prgms.locomocoserver.inquiries.domain;

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
import org.prgms.locomocoserver.global.common.BaseEntity;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;

@Entity
@Getter
@Table(name = "inquiries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogakko_id")
    private Mogakko mogakko;

    @Builder
    public Inquiry(String content, User user, Mogakko mogakko) {
        this.content = content;
        this.user = user;
        this.mogakko = mogakko;
    }

    public void updateInfo(String content) {
        this.content = content;
    }

    public void updateUser(User user) {
        this.user = user;
    }

    public void updateMogakko(Mogakko mogakko) {
        if (Objects.nonNull(mogakko)) {
            mogakko.getInquiries().remove(this);
        }

        this.mogakko = mogakko;
        mogakko.getInquiries().add(this);
    }
}
