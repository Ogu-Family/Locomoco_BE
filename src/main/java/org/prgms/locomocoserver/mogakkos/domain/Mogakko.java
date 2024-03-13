package org.prgms.locomocoserver.mogakkos.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.global.common.BaseEntity;
import org.prgms.locomocoserver.inquiries.domain.Inquiry;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTag;
import org.prgms.locomocoserver.mogakkos.domain.participants.Participant;
import org.prgms.locomocoserver.user.domain.User;

@Entity
@Getter
@Builder
@Table(name = "mogakko")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mogakko extends BaseEntity {

    public static final int DEFAULT_MAX_PARTICIPANTS = 10;

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

    @Column(name = "max_participants", columnDefinition = "int default "
        + DEFAULT_MAX_PARTICIPANTS, nullable = false)
    private int maxParticipants;

    @Column(name = "views")
    private long views;

    @OneToMany(mappedBy = "mogakko", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<MogakkoTag> mogakkoTags = new ArrayList<>();

    @OneToMany(mappedBy = "mogakko")
    @Builder.Default
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "mogakko")
    @Builder.Default
    private List<Inquiry> inquiries = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @OneToOne(mappedBy = "mogakko")
    private ChatRoom chatRoom;

    public Mogakko(Long id, String title, String content, LocalDateTime startTime,
        LocalDateTime endTime, LocalDateTime deadline, int likeCount, int maxParticipants,
        long views, List<MogakkoTag> mogakkoTags, List<Participant> participants,
        List<Inquiry> inquiries, User creator, ChatRoom chatRoom) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deadline = deadline;
        this.likeCount = likeCount;
        this.maxParticipants = maxParticipants;
        this.views = views;
        this.mogakkoTags = mogakkoTags;
        this.participants = participants;
        this.inquiries = inquiries;
        this.creator = creator;
        this.chatRoom = chatRoom;
    }

    public void addMogakkoTag(MogakkoTag mogakkoTag) {
        mogakkoTag.updateMogakko(this);
    }

    public void addParticipant(Participant participant) {
        participant.updateMogakko(this);
    }

    public void addInquiry(Inquiry inquiry) {
        inquiry.updateMogakko(this);
    }

    public void updateCreator(User creator) {
        this.creator = creator;
    }

    public void updateChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public boolean isSameCreatorId(Long creatorId) {
        return this.creator.getId().equals(creatorId);
    }

    public void increaseViews() {
        this.views++;
    }

    public void updateInfo(String title, String content, LocalDateTime startTime, LocalDateTime endTime,
        LocalDateTime deadline, int maxParticipants) {
        this.title = title;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deadline = deadline;
        this.maxParticipants = maxParticipants;
    }

    public void updateLikeCount(boolean flag) {
        if(flag) this.likeCount++;  // 변경 후 값 true -> +1
        else this.likeCount--;  // 변경 후 값 false -> -1
    }
}
