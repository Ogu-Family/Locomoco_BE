package org.prgms.locomocoserver.user.vo;

import lombok.Getter;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;

@Getter
public class EmailVo {

    private String email;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public EmailVo(String email) {
        if (!isValidEmail(email)) {
            throw new UserException(UserErrorType.EMAIL_TYPE_ERROR);
        }
        this.email = email;
    }

    private boolean isValidEmail(String email) {
        return email.matches(EMAIL_REGEX);
    }
}
