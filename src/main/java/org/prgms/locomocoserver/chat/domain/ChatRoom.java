package org.prgms.locomocoserver.chat.domain;

import jakarta.persistence.*;
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "chat_rooms_participants",
            joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "participants_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"chat_room_id", "participants_id"}))
    @Builder.Default
    private List<User> participants = new ArrayList<>();

    public ChatRoom(Long id, String name, Mogakko mogakko, User creator, List<User> participants) {
        this.id = id;
        this.name = name;
        this.mogakko = mogakko;
        this.creator = creator;
        this.participants = participants;
    }

    public void addParticipant(User user) {
        this.participants.add(user);
        user.getChatRoomList().add(this);
    }
}
