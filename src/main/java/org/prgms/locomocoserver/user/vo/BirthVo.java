package org.prgms.locomocoserver.user.vo;

import lombok.Getter;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Getter
public class BirthVo {
    private LocalDate birth;
    private static final String BIRTH_REGEX = "^\\d{4}-\\d{2}-\\d{2}$";

    public BirthVo(LocalDate birth) {
        if (!isValidBirth(birth)) {
            throw new UserException(UserErrorType.BIRTH_TYPE_ERROR);
        }
        this.birth = birth;
    }

    private boolean isValidBirth(LocalDate birth) {
        Pattern pattern = Pattern.compile(BIRTH_REGEX);
        return pattern.matcher(birth.toString()).matches();
    }
}
