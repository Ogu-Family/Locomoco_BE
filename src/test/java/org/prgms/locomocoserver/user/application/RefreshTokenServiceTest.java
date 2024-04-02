package org.prgms.locomocoserver.user.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.user.domain.RefreshTokenRepository;
import org.prgms.locomocoserver.user.dto.response.TokenResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RefreshTokenServiceTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("accessToken을 저장할 수 있다.")
    void saveTokenInfo() {
        // given
        TokenResponseDto tokenResponseDto = new TokenResponseDto("accessToken", "bearer", "refreshToken", 3800, 14000);

        // when
        String accessToken = refreshTokenService.saveTokenInfo(tokenResponseDto);

        // then
        assertEquals("accessToken", accessToken);
    }

    @Test
    void removeRefreshToken() {
    }

    @Test
    void updateRefreshToken() {
    }
}