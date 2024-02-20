package org.prgms.locomocoserver.review.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.review.domain.ReviewRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;


}
