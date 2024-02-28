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
@Table(name = "chat_rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mogakko_id", nullable = false)
    private Mogakko mogakko;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", referencedColumnName = "id", nullable = false)
    private User creator;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<User> participants = new ArrayList<>();

    @Builder
    public ChatRoom(Long id, String name, Mogakko mogakko, User creator) {
        this.id = id;
        this.name = name;
        this.mogakko = mogakko;
        this.creator = creator;
    }

    public void addParticipant(User user) {
        this.participants.add(user);
    }
}
