package org.prgms.locomocoserver.user.dto.response;

import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.domain.enums.Job;
import org.prgms.locomocoserver.user.domain.enums.Provider;

import java.time.LocalDate;

public record UserDto(
        Long userId, String nickname, LocalDate birth, Gender gender, double temperature,
        Job job, String email, Provider provider
) {
}
