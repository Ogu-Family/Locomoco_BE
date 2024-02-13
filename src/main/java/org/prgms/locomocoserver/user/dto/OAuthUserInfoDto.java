package org.prgms.locomocoserver.user.dto;

import org.prgms.locomocoserver.user.domain.User;

public interface OAuthUserInfoDto {
    static double DEFAULT_TEMPERATURE = 36.5;
    String getProvider();
    String getEmail();
    User toEntity();
}
