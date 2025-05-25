package src.application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import src.application.model.Review;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Newly created lists for task 2
    List<Review> findByBeerId(Long beerId);

    Page<Review> findByBeerId(Long beerId, Pageable pageable);

    Page<Review> findByUserId(Long id, Pageable pageable);
    Optional<Review> findByUserIdAndBeerId(Long userId, Long beerId);

    List<Review> findByBeer_Brewery_Id(Long breweryId);
}
