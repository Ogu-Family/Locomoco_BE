package org.prgms.locomocoserver.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgms.locomocoserver.global.common.BaseEntity;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.domain.enums.Job;
import org.prgms.locomocoserver.user.domain.enums.Vendor;
import org.prgms.locomocoserver.user.vo.EmailVo;
import org.prgms.locomocoserver.user.vo.TemperatureVo;

import java.time.LocalDate;
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

    @Column(name = "birth", nullable = false, columnDefinition = "date")
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "temperature", nullable = false)
    private int temperature;

    @Enumerated(EnumType.STRING)
    @Column(name = "job", nullable = false)
    private String job;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "vendor", nullable = false)
    private String vendor;

    @Builder
    public User(String nickname, LocalDate birth, Gender gender, int temperature, Job job, String email, Vendor vendor) {
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender.getDisplayName();
        this.temperature = new TemperatureVo(temperature).getTemperature();
        this.job = job.getDisplayName();
        this.email = new EmailVo(email).getEmail();
        this.vendor = vendor.getDisplayName();
    }
}
