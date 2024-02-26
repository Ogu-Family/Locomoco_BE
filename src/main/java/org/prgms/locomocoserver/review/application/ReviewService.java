package org.prgms.locomocoserver.review.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.review.domain.Review;
import org.prgms.locomocoserver.review.domain.ReviewRepository;
import org.prgms.locomocoserver.review.dto.response.ReviewDto;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<ReviewDto> getRecievedReviews(Long userId) {
        User user = userService.getById(userId);

        return reviewRepository.findAllByRevieweeAndDeletedAtIsNull(user).stream()
                .map(review -> ReviewDto.of(review)).toList();
    }
}
