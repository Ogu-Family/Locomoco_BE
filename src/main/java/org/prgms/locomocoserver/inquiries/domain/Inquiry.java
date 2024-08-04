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
    private static final int MAXIMUM_CONTENT_LENGTH = 200;

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
        validateInfo(content);

        this.content = content;
        this.user = user;
        this.mogakko = mogakko;
    }

    public void updateInfo(String content) {
        validateInfo(content);

        this.content = content;
        this.updateUpdatedAt();
    }

    private static void validateInfo(String content) {
        if (content.length() > MAXIMUM_CONTENT_LENGTH) {
            throw new RuntimeException("문의 내용은 " + MAXIMUM_CONTENT_LENGTH + "자를 초과할 수 없습니다."); // TODO: 문의 예외 반환
        }
    }
}
