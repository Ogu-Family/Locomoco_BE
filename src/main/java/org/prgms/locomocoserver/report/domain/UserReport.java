package org.prgms.locomocoserver.report.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.prgms.locomocoserver.user.domain.User;

@Entity
@Getter
@SuperBuilder
@Table(name = "user_reports")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserReport extends Report {

    @ManyToOne
    @JoinColumn(name = "reported_id")
    private User reportedUser;

    public UserReport(User reportedUser) {
        this.reportedUser = reportedUser;
    }
}
