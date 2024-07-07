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
import org.prgms.locomocoserver.mogakkos.exception.MogakkoErrorType;
import org.prgms.locomocoserver.mogakkos.exception.MogakkoException;
import org.prgms.locomocoserver.user.domain.User;

@Entity
@Getter
@Builder
@Table(name = "mogakko")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mogakko extends BaseEntity {

    public static final int DEFAULT_MAX_PARTICIPANTS = 10;
    public static final int MAX_TITLE_LEN = 255;
    public static final int MAX_CONTENT_LEN = 500;
    public static final String DEFAULT_TITLE = "모각코 모집합니다~";

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
        validate();
    }

    public void addMogakkoTag(MogakkoTag mogakkoTag) {
        mogakkoTag.updateMogakko(this);
    }

    public void addParticipant(Participant participant) {
        participant.updateMogakko(this);
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

    public void updateInfo(String title, String content, LocalDateTime startTime, LocalDateTime endTime,
        LocalDateTime deadline, Integer maxParticipants) {
        this.title = title != null ? title : this.title;
        this.content = content != null ? content : this.content;
        this.startTime = startTime != null ? startTime : this.startTime;
        this.endTime = endTime != null ? endTime : this.endTime;
        this.deadline = deadline != null ? deadline : this.deadline;
        this.maxParticipants = maxParticipants != null ? maxParticipants : this.maxParticipants;
        validate();
    }

    public void updateLikeCount(boolean flag) {
        if(flag) this.likeCount++;  // 변경 후 값 true -> +1
        else this.likeCount--;  // 변경 후 값 false -> -1
    }

    private void validate() {
        this.title = !this.title.isBlank() ? this.title : DEFAULT_TITLE;

        if (this.title.length() > MAX_TITLE_LEN) {
            throw generateCreateException("제목 최대 길이를 초과했습니다!");
        }
        if (this.content.length() > MAX_CONTENT_LEN) {
            throw generateCreateException("내용 최대 길이를 초과했습니다!");
        }
        if (this.startTime.isAfter(this.endTime) || this.deadline.isAfter(this.endTime)) {
            throw generateCreateException("날짜 설정이 잘못되었습니다!");
        }
        if (this.maxParticipants < 0 || this.maxParticipants > DEFAULT_MAX_PARTICIPANTS) {
            throw generateCreateException("최대 인원 수가 잘못되었습니다!");
        }

    }

    private MogakkoException generateCreateException(String msg) {
        return new MogakkoException(
            MogakkoErrorType.CREATE_FORBIDDEN.appendMessage(msg));
    }
}
