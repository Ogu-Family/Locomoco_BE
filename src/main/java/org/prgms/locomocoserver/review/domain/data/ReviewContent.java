package org.prgms.locomocoserver.review.domain.data;

import jakarta.persistence.*;
import lombok.Getter;
import org.prgms.locomocoserver.global.common.BaseEntity;

@Entity
@Getter
@Table(name = "review_contents")
public class ReviewContent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "is_positive")
    private boolean isPositive;

}

