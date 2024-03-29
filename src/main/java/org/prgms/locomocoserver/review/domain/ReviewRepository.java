package org.prgms.locomocoserver.review.domain;

import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByRevieweeAndDeletedAtIsNull(User user);

    List<Review> findAllByReviewerAndDeletedAtIsNull(User user);

    List<Review> findAllByRevieweeAndMogakkoAndDeletedAtIsNull(User reviewee, Mogakko mogakko);
    List<Review> findAllByReviewerAndMogakkoAndDeletedAtIsNull(User reviewer, Mogakko mogakko);

}
