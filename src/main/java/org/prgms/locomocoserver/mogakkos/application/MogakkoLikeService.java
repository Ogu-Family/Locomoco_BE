package org.prgms.locomocoserver.mogakkos.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.likes.MogakkoLike;
import org.prgms.locomocoserver.mogakkos.domain.likes.MogakkoLikeRepository;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoLikeDto;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MogakkoLikeService {

    private final MogakkoLikeRepository likeRepository;
    private final MogakkoService mogakkoService;
    private final UserService userService;

    @Transactional
    public MogakkoLikeDto like(Long mogakkoId, Long userId) {
        Mogakko mogakko = mogakkoService.getByIdNotDeleted(mogakkoId);
        User user = userService.getById(userId);

        if (isLikeMogakko(mogakko, user)) {
            throw new RuntimeException("이미 좋아요한 모각코 입니다.");
        }
        MogakkoLike like = likeRepository.save(MogakkoLike.builder().mogakko(mogakko).user(user).build());
        mogakko.updateLikeCount(true);

        return MogakkoLikeDto.create(like, mogakko.getLikeCount());
    }

    @Transactional
    public MogakkoLikeDto likeCancel(Long mogakkoId, Long userId) {
        Mogakko mogakko = mogakkoService.getByIdNotDeleted(mogakkoId);
        User user = userService.getById(userId);

        MogakkoLike like = likeRepository.findByMogakkoAndUser(mogakko, user)
                .orElseThrow(() -> new RuntimeException("이미 좋아요 취소된 모각코 입니다."));
        likeRepository.delete(like);
        mogakko.updateLikeCount(false);

        return MogakkoLikeDto.create(like, mogakko.getLikeCount());
    }

    private boolean isLikeMogakko(Mogakko mogakko, User user) {
        return likeRepository.existsByMogakkoAndUser(mogakko, user);
    }
}
