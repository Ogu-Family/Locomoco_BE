package org.prgms.locomocoserver.user.application;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.image.application.ImageService;
import org.prgms.locomocoserver.image.domain.Image;
import org.prgms.locomocoserver.image.exception.ImageErrorType;
import org.prgms.locomocoserver.image.exception.ImageException;
import org.prgms.locomocoserver.location.domain.Location;
import org.prgms.locomocoserver.location.domain.LocationRepository;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.likes.MogakkoLikeRepository;
import org.prgms.locomocoserver.mogakkos.domain.participants.ParticipantRepository;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoSimpleInfoResponseDto;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.prgms.locomocoserver.tags.domain.TagRepository;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.dto.OAuthUserInfoDto;
import org.prgms.locomocoserver.user.dto.request.UserInitInfoRequestDto;
import org.prgms.locomocoserver.user.dto.request.UserUpdateRequest;
import org.prgms.locomocoserver.user.dto.response.TokenResponseDto;
import org.prgms.locomocoserver.user.dto.response.UserInfoDto;
import org.prgms.locomocoserver.user.dto.response.UserLoginResponse;
import org.prgms.locomocoserver.user.dto.response.UserMyPageDto;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserService {

    private final UserRepository userRepository;
    private final MogakkoRepository mogakkoRepository;
    private final LocationRepository locationRepository;
    private final MogakkoLikeRepository mogakkoLikeRepository;
    private final ParticipantRepository participantRepository;
    private final TagRepository tagRepository;
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

        return new UserLoginResponse(tokenResponseDto, UserInfoDto.of(user), isNewUser);
    }

    @Transactional
    public UserInfoDto insertInitInfo(Long userId, UserInitInfoRequestDto requestDto, MultipartFile multipartFile) {
        User user = getById(userId);

        Tag jobTag = tagRepository.findById(requestDto.jobId()).orElseThrow(RuntimeException::new); // TODO: 태그 예외 반환
        user.setInitInfo(requestDto.nickname(), requestDto.birth(),
                Gender.valueOf(requestDto.gender().toUpperCase()), jobTag);
        if (multipartFile != null) uploadProfileImage(userId, multipartFile);

        return UserInfoDto.of(user);
    }

    @Transactional
    public UserInfoDto updateUserInfo(Long userId, UserUpdateRequest request, MultipartFile multipartFile) {
        User user = getById(userId);
        Tag jobTag = tagRepository.findById(request.jobId()).orElse(null);

        user.updateUserInfo(request.nickname(), request.birth(), request.gender(), jobTag);

        if(multipartFile != null) uploadProfileImage(userId, multipartFile);

        return UserInfoDto.of(user);
    }

    @Transactional
    public UserInfoDto deleteUser(Long userId) {
        User user = getById(userId);
        user.delete();

        return UserInfoDto.of(user);
    }

    public UserMyPageDto getUserInfo(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        User user = getById(userId);
        long likeMogakkoCount = mogakkoLikeRepository.countByUser(user);
        long ongoingCount = participantRepository.countOngoingByUser(user, now);
        long completeCount = participantRepository.countCompleteByUser(user, now);

        return UserMyPageDto.create(user, likeMogakkoCount, ongoingCount, completeCount);
    }

    public List<MogakkoSimpleInfoResponseDto> getOngoingMogakkos(Long userId) {
        User user = getById(userId);
        List<MogakkoSimpleInfoResponseDto> mogakkoInfoDtos = mogakkoRepository.findOngoingMogakkosByUser(user, LocalDateTime.now())
            .stream().map(mogakko -> {
                Location location = locationRepository.findByMogakkoAndDeletedAtIsNull(mogakko)
                    .orElseThrow(() -> new IllegalArgumentException("Not Found Location"));
                return MogakkoSimpleInfoResponseDto.create(mogakko, location);
            }).toList();

        return mogakkoInfoDtos;
    }

    public List<MogakkoSimpleInfoResponseDto> getCompletedMogakkos(Long userId) {
        User user = getById(userId);
        List<MogakkoSimpleInfoResponseDto> mogakkoInfoDtos = mogakkoRepository.findCompletedMogakkosByUser(user, LocalDateTime.now())
            .stream().map(mogakko -> {
                Location location = locationRepository.findByMogakkoAndDeletedAtIsNull(mogakko)
                    .orElseThrow(() -> new IllegalArgumentException("Not Found Location")); // TODO: 장소 에러 반환
                return MogakkoSimpleInfoResponseDto.create(mogakko, location);
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
