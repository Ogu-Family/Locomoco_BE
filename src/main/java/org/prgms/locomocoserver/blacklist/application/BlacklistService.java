package org.prgms.locomocoserver.blacklist.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.blacklist.domain.Blacklist;
import org.prgms.locomocoserver.blacklist.domain.BlacklistRepository;
import org.prgms.locomocoserver.global.common.dto.Results;
import org.prgms.locomocoserver.user.dto.response.UserBriefInfoDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final BlacklistRepository blacklistRepository;

    @Transactional(readOnly = true)
    public Results<UserBriefInfoDto> findAll(Long cursor, Long userId) {
        List<Blacklist> blacklist = blacklistRepository.findAllByBlockUserId(cursor, userId);

        List<UserBriefInfoDto> blockedUsers = blacklist.stream()
            .map(black -> UserBriefInfoDto.of(black.getBlockedUser())).toList();

        return new Results<>(blockedUsers);
    }
}
