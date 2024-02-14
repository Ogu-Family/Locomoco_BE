package org.prgms.locomocoserver.user.domain.enums;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("남자"),
    FEMALE("여자");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }
}
