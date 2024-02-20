package org.prgms.locomocoserver.review.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.review.domain.data.ReviewContent;
import org.prgms.locomocoserver.review.domain.data.ReviewContentRepository;
import org.prgms.locomocoserver.review.dto.response.ReviewContentDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewContentService {

    private final ReviewContentRepository reviewContentRepository;

    public List<ReviewContentDto> getAllReviewContents() {
        List<ReviewContent> reviewContents = reviewContentRepository.findAllByDeletedAtIsNull();
        return reviewContents.stream()
                .map(reviewContent -> ReviewContentDto.of(reviewContent))
                .collect(Collectors.toList());
    }
}
