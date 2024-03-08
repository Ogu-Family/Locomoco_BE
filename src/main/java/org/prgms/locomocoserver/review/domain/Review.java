package org.prgms.locomocoserver.review.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.global.common.BaseEntity;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.review.domain.data.ReviewContent;
import org.prgms.locomocoserver.user.domain.User;

import java.util.List;

@Entity
@Getter
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id")
    private User reviewee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogakko_id")
    private Mogakko mogakko;

    @Column(name = "score", nullable = false)
    private int score;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "review_content_ids", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "review_contents_id")
    private List<Long> reviewContentIds;

    @Column(name = "content")
    private String content;

    @Builder
    public Review(User reviewer, User reviewee, Mogakko mogakko, int score, List<Long> reviewContentIds, String content) {
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.mogakko = mogakko;
        this.score = score;
        this.reviewContentIds = reviewContentIds;
        this.content = content;
    }
}
