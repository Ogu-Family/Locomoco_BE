package org.prgms.locomocoserver.mogakkos.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "mogakko")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mogakko { // TODO: User 연동

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

    @Column(name = "max_participants", columnDefinition = "int default 10")
    private Integer maxParticipants;

    @Column(name = "location") // TODO: 임시 컬럼. 추후 리스트 구현 시에 Location 테이블과 연동
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private MGCType mgcType;

    @OneToMany(mappedBy = "mogakko", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<MogakkoTag> mogakkoTags = new ArrayList<>();

    public Mogakko(Long id, String title, String content, LocalDateTime startTime,
        LocalDateTime endTime, LocalDateTime deadline, int likeCount, Integer maxParticipants,
        String location, MGCType mgcType, List<MogakkoTag> mogakkoTags) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deadline = deadline;
        this.likeCount = likeCount;
        this.maxParticipants = maxParticipants;
        this.location = location; // TODO: 추후 삭제
        this.mgcType = mgcType;
        this.mogakkoTags = mogakkoTags;
    }

    public void addMogakkoTag(MogakkoTag mogakkoTag) {
        mogakkoTag.updateMogakko(this);
    }
}
