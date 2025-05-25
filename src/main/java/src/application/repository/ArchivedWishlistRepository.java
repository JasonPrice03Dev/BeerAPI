package src.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import src.application.model.ArchivedWishlist;

import java.util.List;

@Repository
public interface ArchivedWishlistRepository extends JpaRepository<ArchivedWishlist, Long> {
    List<ArchivedWishlist> findByUserId(Long userId);
}

