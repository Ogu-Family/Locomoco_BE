package org.prgms.locomocoserver.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatParticipant;
import org.prgms.locomocoserver.global.common.BaseEntity;
import org.prgms.locomocoserver.image.domain.Image;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.vo.BirthVo;
import org.prgms.locomocoserver.user.vo.NicknameVo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "birth", columnDefinition = "date")
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "temperature", nullable = false)
    private double temperature;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "provider", nullable = false)
    private String provider;

    @OneToOne(fetch = FetchType.LAZY)
    private Image profileImage;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<ChatParticipant> chatRooms = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Tag jobTag;

    public User(Long id, String nickname, LocalDate birth, Gender gender, double temperature,
                String email, String provider, Image profileImage, List<ChatParticipant> chatRooms,
                Tag jobTag) {
        this.id = id;
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.temperature = temperature;
        this.email = email;
        this.provider = provider;
        this.profileImage = profileImage;
        this.chatRooms = chatRooms;
        this.jobTag = jobTag;
    }

    public void setInitInfo(String nickname, LocalDate birth, Gender gender, Tag jobTag) {
        this.nickname = new NicknameVo(nickname).getNickname();
        this.birth = new BirthVo(birth).getBirth();
        this.gender = gender;
        this.jobTag = jobTag;
        this.updateUpdatedAt();
    }

    public void updateProfileImage(Image profileImage) {
        this.profileImage = profileImage;
        this.updateUpdatedAt();
    }

    public void updateUserInfo(String nickname, LocalDate birth, Gender gender, Tag jobTag) {
        this.nickname = nickname != null ? nickname : this.nickname;
        this.birth = birth != null ? birth : this.birth;
        this.gender = gender != null ? gender : this.gender;
        this.jobTag = jobTag != null ? jobTag : this.jobTag;
        this.updateUpdatedAt();
    }
}
