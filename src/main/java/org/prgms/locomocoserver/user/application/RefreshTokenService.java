package org.prgms.locomocoserver.user.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.user.domain.RefreshTokenRepository;
import org.prgms.locomocoserver.user.dto.response.TokenResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveTokenInfo(TokenResponseDto tokenResponseDto) {
        refreshTokenRepository.save(tokenResponseDto.toRefreshTokenEntity());
    }

    @Transactional
    public void removeRefreshToken(String accessToken) {

    }

    public void updateRefreshToken(String accessToken) {

    }
}
