package src.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import src.application.model.ArchivedReview;

import java.util.List;

@Repository
public interface ArchivedReviewRepository extends JpaRepository<ArchivedReview, Long> {
    List<ArchivedReview> findByUserId(Long userId);
    List<ArchivedReview> findByBeerId(Long beerId);
}
