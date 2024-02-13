package org.prgms.locomocoserver.user.domain.enums;

import lombok.Getter;

@Getter
public enum Provider {
    KAKAO("카카오"),
    GITHUB("깃허브");

    private final String displayName;

    Provider(String displayName) {
        this.displayName = displayName;
    }
}
