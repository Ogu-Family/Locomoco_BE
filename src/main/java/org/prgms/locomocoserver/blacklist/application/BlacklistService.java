package org.prgms.locomocoserver.blacklist.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.blacklist.domain.Blacklist;
import org.prgms.locomocoserver.blacklist.domain.BlacklistRepository;
import org.prgms.locomocoserver.blacklist.dto.request.BlacklistRequestDto;
import org.prgms.locomocoserver.global.common.dto.Results;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.dto.response.UserBriefInfoDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final BlacklistRepository blacklistRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Results<UserBriefInfoDto> findAll(Long cursor, Long userId) {
        List<Blacklist> blacklist = blacklistRepository.findAllByBlockUserId(cursor, userId);

        List<UserBriefInfoDto> blockedUsers = blacklist.stream()
            .map(black -> UserBriefInfoDto.of(black.getBlockedUser())).toList();

        return new Results<>(blockedUsers);
    }

    public void black(Long userId, BlacklistRequestDto requestDto) {
        User blockUser = userService.getById(userId);
        User blockedUser = userService.getById(requestDto.blockedId());

        validateUser(blockUser, blockedUser);

        Blacklist blacklist = Blacklist.builder().blockUser(blockUser).blockedUser(blockedUser).build();
        blacklistRepository.save(blacklist);
    }

    public void delete(Long userId, Long blockedId) {
        User blockUser = userRepository.getReferenceById(userId);
        User blockedUser = userRepository.getReferenceById(blockedId);

        Blacklist blacklist = blacklistRepository.findByBlockUserAndBlockedUser(blockUser, blockedUser)
            .orElseThrow(() -> new RuntimeException("블랙리스트로 등록되어 있지 않습니다."));

        blacklistRepository.delete(blacklist);
    }

    private void validateUser(User user1, User user2) { // TODO: 이 메서드 추후 따로 클래스로 빼기 (InquiryService에 동일한 메서드 존재)
        if (user1 == user2) {
            throw new RuntimeException("자기 자신은 블랙리스트에 넣을 수 없습니다!"); // TODO: 블랙리스트 예외 반환
        }
    }
}
