package org.prgms.locomocoserver.user.application;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
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
        UserDto userDto = new UserDto(user.getId(), user.getNickname(), user.getBirth().toString(), user.getGender().name(), user.getTemperature(),
                null, user.getEmail(), user.getProvider(), user.getProvideId());
        return userDto;
    }
}
