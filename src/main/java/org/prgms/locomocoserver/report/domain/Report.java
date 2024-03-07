package org.prgms.locomocoserver.report.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.global.common.BaseEntity;
import org.prgms.locomocoserver.user.domain.User;

@Entity
@Getter
@Table(name = "reports")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    User reporter;

    @Column(name = "reproted_id")
    Long reportedId;

    @Column(name = "content")
    String content;

    @Builder
    public Report(User reporter, Long reportedId, String content) {
        this.reporter = reporter;
        this.reportedId = reportedId;
        this.content = content;
    }
}
