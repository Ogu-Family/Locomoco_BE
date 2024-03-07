package org.prgms.locomocoserver.review.domain;

import org.prgms.locomocoserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByRevieweeAndDeletedAtIsNull(User user);
    List<Review> findAllByReviewerAndDeletedAtIsNull(User user);
    List<Review> findAllByMogakkoIdAndDeletedAtIsNull(Long mogakkoId);
}
