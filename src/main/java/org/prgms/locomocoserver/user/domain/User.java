package org.prgms.locomocoserver.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatParticipant;
import org.prgms.locomocoserver.global.common.BaseEntity;
import org.prgms.locomocoserver.image.domain.Image;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.domain.enums.Job;
import org.prgms.locomocoserver.user.dto.request.UserUpdateRequest;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "job")
    private Job job;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "provider", nullable = false)
    private String provider;

    @OneToOne(fetch = FetchType.LAZY)
    private Image profileImage;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<ChatParticipant> chatRooms = new ArrayList<>();

    public User(Long id, String nickname, LocalDate birth, Gender gender, double temperature,
        Job job, String email, String provider, Image profileImage,
        List<ChatParticipant> chatRooms) {
        this.id = id;
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.temperature = temperature;
        this.job = job;
        this.email = email;
        this.provider = provider;
        this.profileImage = profileImage;
        this.chatRooms = chatRooms;
    }

    public void setInitInfo(String nickname, LocalDate birth, Gender gender, Job job) {
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.job = job;
    }

    public void updateProfileImage(Image profileImage) {
        this.profileImage = profileImage;
    }

    public void updateUserInfo(UserUpdateRequest request) {
        if (request.nickname() != null) this.nickname = request.nickname();
        if (request.birth()!= null) this.birth = request.birth();
        if (request.gender()!= null) this.gender = request.gender();
        if (request.job()!= null) this.job = request.job();
    }
}
