package org.prgms.locomocoserver.user.domain.enums;

import lombok.Getter;

@Getter
public enum Vendor {
    kakao("카카오"),
    github("깃허브");

    private final String displayName;

    Vendor(String displayName) {
        this.displayName = displayName;
    }
}
