package org.prgms.locomocoserver.user.dto.request;

import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.domain.enums.Job;

import java.time.LocalDate;

public record UserUpdateRequest(
        String nickname,
        Gender gender,
        LocalDate birth,
        Job job
) {
}
