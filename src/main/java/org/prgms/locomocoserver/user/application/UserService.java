package org.prgms.locomocoserver.user.application;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.location.domain.LocationRepository;
import org.prgms.locomocoserver.location.dto.LocationInfoDto;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTag;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTagRepository;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoInfoDto;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.domain.enums.Job;
import org.prgms.locomocoserver.user.dto.OAuthUserInfoDto;
import org.prgms.locomocoserver.user.dto.request.UserInitInfoRequestDto;
import org.prgms.locomocoserver.user.dto.response.TokenResponseDto;
import org.prgms.locomocoserver.user.dto.response.UserInfoDto;
import org.prgms.locomocoserver.user.dto.response.UserLoginResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserService {

    private final UserRepository userRepository;
    private final MogakkoRepository mogakkoRepository;
    private final LocationRepository locationRepository;
    private final MogakkoTagRepository mogakkoTagRepository;

    @Transactional
    public UserLoginResponse saveOrUpdate(OAuthUserInfoDto oAuthUserInfoDto, TokenResponseDto tokenResponseDto) {
        boolean isNewUser = false;
        Optional<User> optionalUser = userRepository.findByEmailAndDeletedAtIsNull(oAuthUserInfoDto.getEmail());

        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            isNewUser = true;
            user = userRepository.save(oAuthUserInfoDto.toEntity());
        }

        // UserDto 생성
        UserInfoDto userDto = new UserInfoDto(user.getId(), user.getNickname(), user.getBirth(), user.getGender(), user.getTemperature(),
                user.getJob(), user.getEmail(), user.getProvider());
        return new UserLoginResponse(tokenResponseDto, userDto, isNewUser);
    }

    @Transactional
    public UserInfoDto getInitInfo(Long userId, UserInitInfoRequestDto requestDto) {
        User user = getById(userId);
        user.setInitInfo(requestDto.nickname(), requestDto.birth(),
                Gender.valueOf(requestDto.gender().toUpperCase()), Job.valueOf(requestDto.job().toUpperCase()));
        user = userRepository.save(user);

        return new UserInfoDto(user.getId(), user.getNickname(), user.getBirth(), user.getGender(), user.getTemperature(), user.getJob(), user.getEmail(), user.getProvider());
    }

    public UserInfoDto getUserInfo(Long userId) {
        User user = getById(userId);
        return UserInfoDto.of(user);
    }

    public List<MogakkoInfoDto> getOngoingMogakkos(Long userId) {
        User user = getById(userId);
        List<MogakkoInfoDto> mogakkoInfoDtos = mogakkoRepository.findOngoingMogakkosByUser(user, LocalDateTime.now())
                .stream().map(mogakko -> {
                    LocationInfoDto locationInfoDto = LocationInfoDto.create(locationRepository.findByMogakkoAndDeletedAtIsNull(mogakko).orElseThrow(() -> new IllegalArgumentException("Not Found Location")));
                    List<Long> mogakkoTagIds = mogakkoTagRepository.findAllByMogakko(mogakko)
                            .stream().map(mogakkoTag -> mogakkoTag.getId()).toList();  // TODO: List<Long> -> List<MogakkoTags> 변환되면 수정
                    return MogakkoInfoDto.create(mogakko, locationInfoDto, mogakkoTagIds);
                }).toList();

        return mogakkoInfoDtos;
    }

    public List<MogakkoInfoDto> getCompletedMogakkos(Long userId) {
        User user = getById(userId);
        List<MogakkoInfoDto> mogakkoInfoDtos = mogakkoRepository.findCompletedMogakkosByUser(user, LocalDateTime.now())
                .stream().map(mogakko -> {
                    LocationInfoDto locationInfoDto = LocationInfoDto.create(locationRepository.findByMogakkoAndDeletedAtIsNull(mogakko).orElseThrow(() -> new IllegalArgumentException("Not Found Location")));
                    List<Long> mogakkoTagIds = mogakkoTagRepository.findAllByMogakko(mogakko)
                            .stream().map(mogakkoTag -> mogakkoTag.getId()).toList();  // TODO: List<Long> -> List<MogakkoTags> 변환되면 수정
                    return MogakkoInfoDto.create(mogakko, locationInfoDto, mogakkoTagIds);
                }).toList();

        return mogakkoInfoDtos;
    }

    public boolean isNicknameUnique(String nickname) {
        return !userRepository.findByNicknameAndDeletedAtIsNull(nickname).isPresent();
    }

    public User getById(Long userId) {
        return userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    }
}
