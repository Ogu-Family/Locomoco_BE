package org.prgms.locomocoserver.mogakkos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "mogakko")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mogakko {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", columnDefinition = "varchar(255) default '모각코 모집합니다~'")
    private String title;

    @Column(name = "content", columnDefinition = "varchar(500)")
    private String content;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "max_participant", columnDefinition = "int default 10")
    private Integer max_participant;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    @Builder
    public Mogakko(String title, String content, LocalDateTime startTime, LocalDateTime endTime,
        LocalDateTime deadline, int likeCount, User creator) {
        this.title = title;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deadline = deadline;
        this.likeCount = likeCount;
        this.creator = creator;
    }
}
