package org.prgms.locomocoserver.review.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.application.MogakkoService;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.review.domain.Review;
import org.prgms.locomocoserver.review.domain.ReviewRepository;
import org.prgms.locomocoserver.review.dto.request.ReviewCreateRequestDto;
import org.prgms.locomocoserver.review.dto.response.ReviewDto;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Arrays.stream;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final MogakkoService mogakkoService;

    @Transactional
    public ReviewDto create(Long mogakkoId, Long reviewerId, ReviewCreateRequestDto requestDto) {
        User reviewer = userService.getById(reviewerId);
        User reviewee = userService.getById(requestDto.revieweeId());
        Mogakko mogakko = mogakkoService.getByIdNotDeleted(mogakkoId);

        Review review = reviewRepository.save(ReviewCreateRequestDto.create(mogakko, reviewer, reviewee, requestDto));

        return ReviewDto.of(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getRecievedReviews(Long userId) {
        User user = userService.getById(userId);

        return reviewRepository.findAllByRevieweeAndDeletedAtIsNull(user).stream()
                .map(review -> ReviewDto.of(review)).toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getSentReviews(Long userId) {
        User user = userService.getById(userId);

        return reviewRepository.findAllByReviewerAndDeletedAtIsNull(user).stream()
                .map(review -> ReviewDto.of(review)).toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getMogakkoReviewsRecieved(Long revieweeId, Long mogakkoId) {
        User reviewee = userService.getById(revieweeId);
        Mogakko mogakko = mogakkoService.getByIdNotDeleted(mogakkoId);

        List<ReviewDto> reviewDtos = reviewRepository.findAllByRevieweeAndMogakkoAndDeletedAtIsNull(reviewee, mogakko).stream()
                .map(review -> ReviewDto.of(review)).toList();

        return reviewDtos;
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getMogakkoReviewsSent(Long reviewerId, Long mogakkoId) {
        User reviewer = userService.getById(reviewerId);
        Mogakko mogakko = mogakkoService.getByIdNotDeleted(mogakkoId);

        List<ReviewDto> reviewDtos = reviewRepository.findAllByReviewerAndMogakkoAndDeletedAtIsNull(reviewer, mogakko).stream()
                .map(review -> ReviewDto.of(review)).toList();

        return reviewDtos;
    }
}
