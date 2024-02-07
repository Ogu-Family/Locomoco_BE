package org.prgms.locomocoserver.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.global.common.BaseEntity;
import org.prgms.locomocoserver.user.vo.EmailVo;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "birth", nullable = false)
    private LocalDateTime birth;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "temperature", nullable = false)
    private int temperature;

    @Column(name = "job", nullable = false)
    private String job;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "vendor", nullable = false)
    private String vendor;

    @Builder
    public User(String nickname, LocalDateTime birth, String gender, int temperature, String job, String email, String vendor) {
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.temperature = temperature;
        this.job = job;
        this.email = new EmailVo(email).getEmail();
        this.vendor = vendor;
    }
}
