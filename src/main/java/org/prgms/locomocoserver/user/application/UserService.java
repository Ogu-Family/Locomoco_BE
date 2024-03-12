package org.prgms.locomocoserver.user.application;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.image.application.ImageService;
import org.prgms.locomocoserver.image.domain.Image;
import org.prgms.locomocoserver.image.dto.ImageDto;
import org.prgms.locomocoserver.image.exception.ImageErrorType;
import org.prgms.locomocoserver.image.exception.ImageException;
import org.prgms.locomocoserver.location.domain.Location;
import org.prgms.locomocoserver.location.domain.LocationRepository;
import org.prgms.locomocoserver.location.dto.LocationInfoDto;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.likes.MogakkoLikeRepository;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTagRepository;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoInfoDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoSimpleInfoResponseDto;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.domain.enums.Job;
import org.prgms.locomocoserver.user.dto.OAuthUserInfoDto;
import org.prgms.locomocoserver.user.dto.request.UserInitInfoRequestDto;
import org.prgms.locomocoserver.user.dto.request.UserUpdateRequest;
import org.prgms.locomocoserver.user.dto.response.TokenResponseDto;
import org.prgms.locomocoserver.user.dto.response.UserInfoDto;
import org.prgms.locomocoserver.user.dto.response.UserLoginResponse;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserService {

    private final UserRepository userRepository;
    private final MogakkoRepository mogakkoRepository;
    private final LocationRepository locationRepository;
    private final MogakkoTagRepository mogakkoTagRepository;
    private final MogakkoLikeRepository mogakkoLikeRepository;
    private final ImageService imageService;

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
                user.getJob(), user.getEmail(), ImageDto.of(user.getProfileImage()), user.getProvider());
        return new UserLoginResponse(tokenResponseDto, userDto, isNewUser);
    }

    @Transactional
    public UserInfoDto insertInitInfo(Long userId, UserInitInfoRequestDto requestDto, MultipartFile multipartFile) {
        User user = getById(userId);

        user.setInitInfo(requestDto.nickname(), requestDto.birth(),
                Gender.valueOf(requestDto.gender().toUpperCase()), Job.valueOf(requestDto.job().toUpperCase()));
        if (multipartFile != null) uploadProfileImage(userId, multipartFile);

        return UserInfoDto.of(user);
    }

    @Transactional
    public UserInfoDto updateUserInfo(Long userId, UserUpdateRequest request, MultipartFile multipartFile) {
        User user = getById(userId);
        user.updateUserInfo(request);

        if(multipartFile != null) uploadProfileImage(userId, multipartFile);

        return UserInfoDto.of(user);
    }

    @Transactional
    public UserInfoDto deleteUser(Long userId) {
        User user = getById(userId);
        user.delete();

        return UserInfoDto.of(user);
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

    @Transactional(readOnly = true)
    public List<MogakkoSimpleInfoResponseDto> getLikedMogakkos(Long userId) {
        User user = getById(userId);
        List<MogakkoSimpleInfoResponseDto> mogakkoInfoDtos = mogakkoLikeRepository.findAllByUser(user).stream()
                .map(mogakkoLike -> {
                     Mogakko mogakko = mogakkoLike.getMogakko();
                     Location location = locationRepository.findByMogakkoAndDeletedAtIsNull(mogakko)
                             .orElseThrow(() -> new IllegalArgumentException("Not Found Location"));  // TODO : 장소 예외 반환
                     return MogakkoSimpleInfoResponseDto.create(mogakko, location);
                }).toList();

        return mogakkoInfoDtos;
    }

    @Transactional
    public UserInfoDto uploadProfileImage(Long userId, MultipartFile multipartFile) {
        try {
            User loginUser = getById(userId);

            Image pre = loginUser.getProfileImage();
            if(pre != null) {
                imageService.remove(pre);
            }

            Image image = imageService.upload(multipartFile);
            loginUser.updateProfileImage(image);

            return UserInfoDto.of(loginUser);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ImageException(ImageErrorType.FILE_WRITE_ERROR);
        }
    }

    @Transactional
    public UserInfoDto deleteProfileImage(Long userId) {
        User user = getById(userId);
        Image profileImage = user.getProfileImage();

        if(profileImage != null) imageService.remove(profileImage);
        else throw new ImageException(ImageErrorType.IMAGE_NOT_FOUND);

        return UserInfoDto.of(user);
    }

    public boolean isNicknameUnique(String nickname) {
        return !userRepository.findByNicknameAndDeletedAtIsNull(nickname).isPresent();
    }

    public User getById(Long userId) {
        return userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new UserException(UserErrorType.USER_NOT_FOUND));
    }
}
