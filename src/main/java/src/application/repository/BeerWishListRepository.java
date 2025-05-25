package src.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import src.application.model.BeerWishList;

import java.util.Optional;

public interface BeerWishListRepository extends JpaRepository<BeerWishList, Long> {
    Optional<BeerWishList> findByUserId(Long userId);
}
