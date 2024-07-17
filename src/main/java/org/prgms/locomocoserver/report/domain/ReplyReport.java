package org.prgms.locomocoserver.report.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.prgms.locomocoserver.global.common.BaseEntity;
import org.prgms.locomocoserver.replies.domain.Reply;
import org.prgms.locomocoserver.user.domain.User;

@Entity
@Getter
@Table(name = "reply_reports")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply reply;

    @Column(name = "content")
    private String content;

    @Builder
    public ReplyReport(User reporter, Reply reply, String content) {
        this.reporter = reporter;
        this.reply = reply;
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
