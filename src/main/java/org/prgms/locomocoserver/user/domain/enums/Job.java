package org.prgms.locomocoserver.user.domain.enums;

import lombok.Getter;

@Getter
public enum Job {
    etc("기타"),
    developer("현직자"),
    job_seeker("취준생");

    private final String displayName;

    Job(String displayName) {
        this.displayName = displayName;
    }
}
