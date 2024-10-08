package org.prgms.locomocoserver.chat.domain;

import jakarta.persistence.*;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.global.common.BaseEntity;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@Table(name = "chat_rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogakko_id", nullable = false, unique = true)
    private Mogakko mogakko;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", referencedColumnName = "id", nullable = false)
    private User creator;

    @OneToMany(mappedBy = "chatRoom", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<ChatParticipant> chatParticipants = new ArrayList<>();

    public ChatRoom(Long id, String name, Mogakko mogakko, User creator, List<ChatParticipant> chatParticipants) {
        this.id = id;
        this.name = name;
        this.mogakko = mogakko;
        this.creator = creator;
        this.chatParticipants = chatParticipants;
    }

    public void addChatParticipant(ChatParticipant participant) {
        if (Objects.nonNull(participant)) {
            this.chatParticipants.remove(participant);
        }

        this.chatParticipants.add(participant);
        this.updateUpdatedAt();
    }
}
