package org.prgms.locomocoserver.user.dto;

import org.prgms.locomocoserver.user.domain.User;

public interface OAuthUserInfoDto {
    String getProvider();
    String getEmail();
    User toEntity();
}
