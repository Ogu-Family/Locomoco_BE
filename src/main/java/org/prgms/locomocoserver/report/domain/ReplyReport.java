package org.prgms.locomocoserver.report.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.prgms.locomocoserver.replies.domain.Reply;
import org.prgms.locomocoserver.user.domain.User;

@Entity
@Getter
@SuperBuilder
@Table(name = "reply_reports")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyReport extends Report {

    @ManyToOne
    @JoinColumn(name = "reply_id")
    private Reply reply;

    public ReplyReport(User reporter, Reply reply, String content) {
        this.reply = reply;
    }
}
