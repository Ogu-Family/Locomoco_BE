package org.prgms.locomocoserver.user.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByAccessToken(String accessToken);
    void deleteByAccessToken(String refreshToken);
}
