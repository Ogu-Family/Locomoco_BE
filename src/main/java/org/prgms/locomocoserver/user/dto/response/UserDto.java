package org.prgms.locomocoserver.user.dto.response;

public record UserDto(
        Long userId, String nickname, String birth, String gender, double temperature,
        String job, String email, String provider
) {
}
