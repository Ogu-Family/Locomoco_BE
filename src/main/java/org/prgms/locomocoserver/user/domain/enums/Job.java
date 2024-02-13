package org.prgms.locomocoserver.user.domain.enums;

import lombok.Getter;

@Getter
public enum Job {
    ETC("기타"),
    DEVELOPER("현직자"),
    JOB_SEEKER("취준생");

    private final String displayName;

    Job(String displayName) {
        this.displayName = displayName;
    }
}
