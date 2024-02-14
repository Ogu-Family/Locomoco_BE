package org.prgms.locomocoserver.user.application;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.domain.enums.Provider;
import org.prgms.locomocoserver.user.dto.OAuthUserInfoDto;
import org.prgms.locomocoserver.user.dto.response.UserDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDto saveOrUpdate(OAuthUserInfoDto userInfoDto) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(userInfoDto.getEmail())
                .orElseGet(() -> userRepository.save(userInfoDto.toEntity()));

        // UserDto 생성
        UserDto userDto = new UserDto(user.getId(), user.getNickname(), user.getBirth(), user.getGender(), user.getTemperature(),
                user.getJob(), user.getEmail(), Provider.valueOf(user.getProvider()));
        return userDto;
    }
}
