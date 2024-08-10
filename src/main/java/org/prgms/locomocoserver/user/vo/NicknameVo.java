package org.prgms.locomocoserver.user.vo;

import lombok.Getter;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;

import java.util.regex.Pattern;

@Getter
public class NicknameVo {

    private static final String NICKNAME_REGEX = "^[a-zA-Z0-9가-힣]{2,10}$";

    private String nickname;

    public NicknameVo(String nickname) {
        if(isValidNickName(nickname)) throw new UserException(UserErrorType.NICKNAME_TYPE_ERROR);
        this.nickname = nickname;
    }

    private boolean isValidNickName(String nickname) {
        Pattern pattern = Pattern.compile(NICKNAME_REGEX);
        return pattern.matcher(nickname).matches();
    }
}
