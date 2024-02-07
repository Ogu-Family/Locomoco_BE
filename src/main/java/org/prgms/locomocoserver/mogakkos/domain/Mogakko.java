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

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "varchar(500)")
    private String content;

    @Column(name = "start")
    private LocalDateTime start;

    @Column(name = "end")
    private LocalDateTime end;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "like")
    private int like;

    @Column(name = "max_participant")
    private int max_participant;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    @Builder
    public Mogakko(Long id, String title, String content, LocalDateTime start, LocalDateTime end,
        LocalDateTime deadline, int like, User creator) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.start = start;
        this.end = end;
        this.deadline = deadline;
        this.like = like;
        this.creator = creator;
    }
}
