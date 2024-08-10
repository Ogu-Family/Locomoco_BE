package org.prgms.locomocoserver.user.vo;

import lombok.Getter;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.regex.Pattern;

@Getter
public class BirthVo {
    private LocalDate birth;
    private static final String BIRTH_REGEX = "^\\d{4}-\\d{2}-\\d{2}$";
    private static final int REQUIRED_AGE = 12;

    public BirthVo(LocalDate birth) {
        if (!isValidBirth(birth)) {
            throw new UserException(UserErrorType.BIRTH_TYPE_ERROR);
        }
        if(!isAtLeast12YearsOld(birth)) {
            throw new UserException(UserErrorType.AGE_NOT_ENOUGH);
        }
        this.birth = birth;
    }

    private boolean isAtLeast12YearsOld(LocalDate birth) {
        LocalDate today = LocalDate.now();
        int age = Period.between(birth, today).getYears();
        return age >= REQUIRED_AGE;
    }

    private boolean isValidBirth(LocalDate birth) {
        Pattern pattern = Pattern.compile(BIRTH_REGEX);
        return pattern.matcher(birth.toString()).matches();
    }
}
